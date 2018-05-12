/*_############################################################################
  _##
  _##  SNMP4J-AgentX - BufferedMOTableModel.java
  _##
  _##  Copyright (C) 2005-2014  Frank Fock (SNMP4J.org)
  _##
  _##  This program is free software; you can redistribute it and/or modify
  _##  it under the terms of the GNU General Public License version 2 as
  _##  published by the Free Software Foundation.
  _##
  _##  This program is distributed in the hope that it will be useful,
  _##  but WITHOUT ANY WARRANTY; without even the implied warranty of
  _##  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  _##  GNU General Public License for more details.
  _##
  _##  You should have received a copy of the GNU General Public License
  _##  along with this program; if not, write to the Free Software
  _##  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
  _##  MA  02110-1301  USA
  _##
  _##########################################################################*/

package org.snmp4j.agent.mo;

import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

import java.util.*;

/**
 * The BufferedMOTableModel is a read-only table model that dynamically loads data from an
 * backend as needed. The internal buffer holds up to {@link #getMaxBufferSize()} rows fetched from the backend.
 * Rows are removed from the buffer either when they are older than {@link #getBufferTimeoutNanoSeconds()} or
 * if the maximum number of buffered rows is reached.
 * <p>
 *   The {@link org.snmp4j.agent.mo.MOTableRowFactory} is needed to create table rows from the
 *   backend plain row data.
 * </p>
 *
 * @author Frank Fock
 * @since 2.2
 */
public abstract class BufferedMOTableModel<R extends MOTableRow> implements MOTableModel<R> {

  protected final SortedMap<OID, BufferedMOTableRow<R>> bufferedRows =
      Collections.synchronizedSortedMap(new TreeMap<OID, BufferedMOTableRow<R>>());
  protected final List<BufferedMOTableRow<R>> bufferedChunksList =
      Collections.synchronizedList(new LinkedList<BufferedMOTableRow<R>>());
  protected BufferedMOTableRow<R> firstRow;
  protected BufferedMOTableRow<R> lastRow;

  protected MOTableRowFactory<R> rowFactory;

  /**
   * The number of columns in this table model.
   */
  protected int columnCount = 0;

  /**
   * The timeout for buffered rows in 10^-9 seconds.
   */
  protected long bufferTimeoutNanoSeconds = 5L * SnmpConstants.MILLISECOND_TO_NANOSECOND * 1000L;

  /**
   * The number of rows that are fetched with {@link #fetchNextRows(org.snmp4j.smi.OID, int)} consecutively to
   * minimize the number of backend read operations.
   */
  protected int chunkSize = 10;

  /**
   * The maximum number of rows in the buffer. While requests processing the actual upper bound of the buffer
   * might be the sum of {@link #getMaxBufferSize()} and {@link #getChunkSize()}.
   */
  protected int maxBufferSize = 100;

  /**
   * The number of rows that were read from the buffer without backend access.
   */
  protected long bufferHits = 0;

  /**
   * The number of rows that were read from the backend, because they were not found or not valid in the buffer.
   */
  protected long bufferMisses = 0;

  /**
   * Creates a BufferedMOTableModel with the specified {@link org.snmp4j.agent.mo.MOTableRowFactory}.
   * @param rowFactory
   *    the row factory to be used to create rows from backend data.
   */
  protected BufferedMOTableModel(MOTableRowFactory<R> rowFactory) {
    this.rowFactory = rowFactory;
  }

  /**
   * Sets the factory instance to be used for creating rows for this model.
   *
   * @param rowFactory
   *    a {@code MOTableRowFactory} instance or {@code null} to
   *    disable row creation.
   * @param <F>
   *    the {@link MOTableRowFactory} class creating rows of type R.
   */
  public  <F extends MOTableRowFactory<R>>  void setRowFactory(F rowFactory) {
    this.rowFactory = rowFactory;
  }

  public <F extends MOTableRowFactory<R>> F getRowFactory() {
    return (F) rowFactory;
  }

  /**
   * Gets the timeout nano-seconds for buffered rows.
   * @return
   *    the number of nano-seconds after which an buffered row is invalidated and removed from the buffer.
   */
  public long getBufferTimeoutNanoSeconds() {
    return bufferTimeoutNanoSeconds;
  }

  /**
   * Sets the timeout nano-seconds for buffered rows.
   * @param bufferTimeoutNanoSeconds
   *    the number of nano-seconds after which an buffered row is invalidated and removed from the buffer.
   */
  public void setBufferTimeoutNanoSeconds(long bufferTimeoutNanoSeconds) {
    this.bufferTimeoutNanoSeconds = bufferTimeoutNanoSeconds;
  }

  /**
   * Returns the maximum number of rows in the buffer.
   * @return
   *    the size of the row buffer.
   */
  public int getMaxBufferSize() {
    return maxBufferSize;
  }

  /**
   * Sets the maximum number of rows in the buffer. The default is
   * @param maxBufferSize
   *    the size of the row buffer.
   */
  public void setMaxBufferSize(int maxBufferSize) {
    this.maxBufferSize = maxBufferSize;
  }

  /**
   * Returns the chunk size for GETNEXT like buffer fetching with the {@link #fetchNextRows(org.snmp4j.smi.OID, int)}
   * operation. The default is 10.
   * @return
   *    the chunk size for consecutive row fetching from the backend.
   */
  public int getChunkSize() {
    return chunkSize;
  }

  /**
   * Sets the chunk size for GETNEXT like buffer fetching with the {@link #fetchNextRows(org.snmp4j.smi.OID, int)}
   * operation. The default is 10.
   * @param chunkSize
   *    the chunk size for consecutive row fetching from the backend.
   */
  public void setChunkSize(int chunkSize) {
    this.chunkSize = chunkSize;
  }

  @Override
  public int getColumnCount() {
    return columnCount;
  }

  @Override
  public abstract int getRowCount();

  @Override
  public abstract boolean containsRow(OID index);

  @Override
  public R getRow(OID index) {
    return getRow(index, true);
  }

  /**
   * Gets a row from the internal buffer or the backend and puts it into the buffer if specified.
   * @param index
   *    the index of the target row.
   * @param putRowIntoBuffer
   *    if <code>true</code> then the fetched row (not from the buffer) will be put into the buffer.
   * @return
   *    the row with the given index or <code>null</code> if it does not exist.
   */
  protected R getRow(OID index, boolean putRowIntoBuffer) {
    BufferedMOTableRow<R> row = getRowFromBuffer(index);
    if (row == null) {
      bufferMisses++;
      Variable[] rowValues = fetchRow(index);
      if (rowValues != null) {
        R r = rowFactory.createRow(index, rowValues);
        row = new BufferedMOTableRow<R>(r);
      }
      else {
        row = new BufferedMOTableRow<R>(null);
      }
      if (putRowIntoBuffer) {
        bufferedRows.put(index, row);
      }
    }
    else {
      bufferHits++;
    }
    return row.getBufferedRow();
  }

  @Override
  public Iterator<R> iterator() {
    return tailIterator(null);
  }

  @Override
  public Iterator<R> tailIterator(OID lowerBound) {
    return new RowBufferIterator(lowerBound);
  }

  @Override
  public abstract OID lastIndex();

  @Override
  public abstract OID firstIndex();

  @Override
  public R firstRow() {
    BufferedMOTableRow<R> firstBufferRow = getFirstBufferRow();
    if (firstBufferRow != null) {
      return firstBufferRow.getBufferedRow();
    }
    return null;
  }

  /**
   * Gets the first row from the buffer or the backend if not available from the buffer.
   * @return
   *    the first row as buffered row.
   */
  protected synchronized BufferedMOTableRow<R> getFirstBufferRow() {
    if (firstRow != null) {
      if (isRowValid(firstRow.getLastRefresh())) {
        return firstRow;
      }
      else {
        bufferedRows.remove(firstRow.getIndex());
      }
    }
    OID firstIndex = firstIndex();
    if (firstIndex != null) {
      R row = getRow(firstIndex, true);
      if (row != null) {
        this.firstRow = getRowFromBuffer(firstIndex);
      }
      return firstRow;
    }
    return null;
  }

  @Override
  public R lastRow() {
    BufferedMOTableRow<R> lastBufferRow = getLastBufferRow();
    if (lastBufferRow != null) {
      return lastBufferRow.getBufferedRow();
    }
    return null;
  }

  /**
   * Gets the last row from the buffer or the backend if not available from the buffer.
   * @return
   *    the last row as buffered row.
   */
  protected synchronized BufferedMOTableRow<R> getLastBufferRow() {
    if (lastRow != null) {
      if (isRowValid(lastRow.getLastRefresh())) {
        return lastRow;
      }
      else {
        bufferedRows.remove(lastRow.getIndex());
      }
    }
    OID lastIndex = lastIndex();
    if (lastIndex != null) {
      // do not put into queue to avoid
      R row = getRow(lastIndex, false);
      if (row != null) {
        this.lastRow = getRowFromBuffer(lastIndex);
      }
      return lastRow;
    }
    return null;
  }

  /**
   * Tests if the given timestamp (in nano seconds as retrieved from {@link System#nanoTime()}) denotes
   * a valid row.
   * @param lastRefreshNanoTime
   *    the last refresh timestamp from a buffered row.
   * @return
   *    {@code true} if {@code (System.nanoTime() - lastRefreshNanoTime &lt; bufferTimeoutNanoSeconds)}
   */
  protected boolean isRowValid(long lastRefreshNanoTime) {
    return (System.nanoTime() - lastRefreshNanoTime < bufferTimeoutNanoSeconds);
  }

  /**
   * Gets a row from the buffer.
   * @param index
   *   the index of the target row.
   * @return
   *   {@code null} if a row with the given index is not buffered (or no longer valid) and
   *   the buffered row with that index if that row could be found and is still valid.
   */
  protected BufferedMOTableRow<R> getRowFromBuffer(OID index) {
    BufferedMOTableRow<R> row = bufferedRows.get(index);
    if (row != null) {
      if (!isRowValid(row.getLastRefresh())) {
        bufferedRows.remove(index);
        return null;
      }
      return row;
    }
    return null;
  }

  /**
   * Fetches the specified row from the backend source.
   * @param index
   *    the rows index OID value.
   * @return
   *    the values of the fetched row or {@code null} if the row does not exists.
   */
  protected abstract Variable[] fetchRow(OID index);

  /**
   * Fetches a list of rows from the backend source.
   * @param lowerBound
   *   the lower bound index (inclusive) of the first row to return.
   * @param chunkSize
   *   the maximum number of rows to return. Less rows may be returned even if there are more
   *   available.
   * @return
   *   a list of rows fetched from the backend. If the last element of the returned list is
   *   {@code null}, no more rows are available at the source.
   */
  protected abstract List<R> fetchNextRows(OID lowerBound, int chunkSize);

  /**
   * Removes a row from the table. By default this method throws a {@link java.lang.UnsupportedOperationException}.
   * @param index
   *    the index of the target row.
   * @return
   *    the removed row and {@code null} if such a row does not exist.
   */
  public R removeRow(OID index) {
    throw new UnsupportedOperationException();
  }

  /**
   * The RowBufferIterator implements the iterator needed by the {@link org.snmp4j.agent.mo.MOTableModel} to
   * traverse the model's rows.
   */
  protected class RowBufferIterator implements Iterator<R> {

    private OID currentIndex;
    private BufferedMOTableRow<R> nextRow;

    /**
     * Creates the iterator with the specified start row.
     * @param lowerBound
     *   the lower bound index (inclusive) of the first row to return. If {@code null} is specified, the
     *   first row will be returned by first call of {@link #next()}.
     */
    public RowBufferIterator(OID lowerBound) {
      this.currentIndex = lowerBound;
      if (currentIndex == null) {
        nextRow = getFirstBufferRow();
      }
      else {
        nextRow = fetchNextBufferRow(currentIndex, null, true);
      }
    }

    @Override
    public boolean hasNext() {
      return nextRow != null;
    }

    @Override
    public R next() {
      BufferedMOTableRow<R> next = nextRow;
      if (next != null) {
        nextRow = fetchNextBufferRow(next.getIndex(), nextRow, false);
        return next.getBufferedRow();
      }
      else {
        throw new NoSuchElementException();
      }
    }

    @Override
    public void remove() {
      removeRow(currentIndex);
    }

    protected BufferedMOTableRow<R> fetchNextBufferRow(OID lowerBound, BufferedMOTableRow<R> predecessor,
                                                       boolean includeLowerBound) {
      if (predecessor != null) {
        BufferedMOTableRow<R> next = predecessor.getNextRow();
        if ((next != null) && (isRowValid(next.getLastRefresh()))) {
          while (next != null && next.getBufferedRow() == null) {
            next = (isRowValid(next.getLastRefresh())) ? next.getNextRow() : null;
          }
          if (next != null) {
            bufferHits++;
            return next;
          }
        }
      }
      OID lowerBoundInc = (includeLowerBound) ? lowerBound : lowerBound.successor();
      synchronized (bufferedRows) {
        SortedMap<OID,BufferedMOTableRow<R>> nextRows = bufferedRows.tailMap(lowerBoundInc);
        if (!nextRows.isEmpty()) {
          BufferedMOTableRow<R> bufferedRow = null;
          for (BufferedMOTableRow<R> row : nextRows.values()) {
            bufferedRow = row;
            if (bufferedRow.getIndex() != null) {
              break;
            }
          }
          if ((bufferedRow != null) && isRowValid(bufferedRow.getLastRefresh())) {
            if (includeLowerBound && bufferedRow.getIndex().equals(lowerBoundInc)) {
              bufferHits++;
              return bufferedRow;
            }
            else if (bufferedRow.getNextRow() != null) {
              // validate that there is no intermediate row in backend table
              List<R> newBufferRows = fetchNextRows(lowerBoundInc, 1);
              if (newBufferRows != null && !newBufferRows.isEmpty() &&
                  newBufferRows.get(0).getIndex().equals(bufferedRow.getIndex())) {
                bufferMisses++;
                bufferedRow.setBufferedRow(newBufferRows.get(0));
                bufferedRow.setLastRefresh(System.nanoTime());
                return bufferedRow;
              }
            }
          }
        }
      }
      bufferMisses++;
      List<R> newBufferRows = fetchNextRows(lowerBoundInc, chunkSize);
      return updateBuffer(newBufferRows, predecessor);
    }
  }

  /**
   * Updates the internal buffer with a list of consecutive rows.
   * @param newBufferRows
   *    a list of rows which must be in lexicographic order (regarding their index values) and
   *    without holes.
   * @param predecessor
   *    the preceding row of the buffer area that needs to be updated.
   * @return
   *    the buffer row of the first row in the list. As buffer rows are linked by
   *    {@link org.snmp4j.agent.mo.BufferedMOTableModel.BufferedMOTableRow#getNextRow()}
   *    the buffered rows can be fully traversed by using that first row.
   */
  protected BufferedMOTableRow<R> updateBuffer(List<R> newBufferRows, BufferedMOTableRow<R> predecessor) {
    BufferedMOTableRow<R> firstBufferRow = null;
    BufferedMOTableRow<R> lastBufferRow = null;
    for (R row : newBufferRows) {
      BufferedMOTableRow<R> bufferRow = new BufferedMOTableRow<R>(row);
      bufferedRows.put(row.getIndex(), bufferRow);
      if (lastBufferRow != null) {
        lastBufferRow.setNextRow(bufferRow);
      }
      if (firstBufferRow == null) {
        firstBufferRow = bufferRow;
        if (predecessor != null) {
          predecessor.setNextRow(firstBufferRow);
        }
      }
      lastBufferRow = bufferRow;
    }
    if (firstBufferRow != null) {
      bufferedChunksList.add(firstBufferRow);
    }
    if (bufferedRows.size() > maxBufferSize) {
      cleanupBuffer();
    }
    return firstBufferRow;
  }

  /**
   * Removes any rows from the buffer that exceed the buffer's size limit (FIFO).
   * @return
   *    the number of rows actually removed from the buffer.
   */
  protected synchronized int cleanupBuffer() {
    int removed = 0;
    int oversize = bufferedRows.size() - maxBufferSize;
    if (oversize > 0) {
      for (int i=oversize; (i>=0) && (!bufferedChunksList.isEmpty()); i--) {
        BufferedMOTableRow<R> disposeRow = bufferedChunksList.remove(0);
        if (disposeRow != null) {
          bufferedRows.remove(disposeRow.getIndex());
          removed++;
          for (disposeRow = disposeRow.getNextRow(); (disposeRow != null) && (--i>0); disposeRow = disposeRow.getNextRow()) {
            bufferedRows.remove(disposeRow.getIndex());
            if ((!bufferedChunksList.isEmpty()) && disposeRow.getIndex().equals(bufferedChunksList.get(0).getIndex())) {
              bufferedChunksList.remove(0);
            }
            removed++;
          }
        }
      }
    }
    return removed;
  }

  /**
   * Returns the number of row accesses that were served by the buffer.
   * @return
   *    the number of row accesses through the buffered.
   */
  public long getBufferHits() {
    return bufferHits;
  }

  /**
   * The number of row accesses that could not be served from the buffer and thus
   * needed backend processing.
   * @return
   *    the number of buffer misses.
   */
  public long getBufferMisses() {
    return bufferMisses;
  }

  /**
   * Removes all rows from the internal buffer. This method is synchronized to ensure a consistent buffer.
   * It can be used to force a full buffer refresh.
   */
  public synchronized void resetBuffer() {
    bufferedRows.clear();
    bufferedChunksList.clear();
    firstRow = null;
    lastRow = null;
  }

  @Override
  public String toString() {
    return "BufferedMOTableModel{" +
        "columnCount=" + columnCount +
        ", chunkSize=" + chunkSize +
        ", bufferTimeoutNanoSeconds=" + bufferTimeoutNanoSeconds +
        ", maxBufferSize=" + maxBufferSize +
        ", bufferHits=" + bufferHits +
        ", bufferMisses=" + bufferMisses +
        ", rowFactory=" + rowFactory +
        '}';
  }

  /**
   * The BufferedMOTableRow is a wrapper class that holds additional information for the buffering.
   * It implements a forward linked list to direct successor rows for fast and easy traversal of the
   * buffer.
   *
   * @param <R>
   *   the payload row class which has to match the models row class as created by {@link #rowFactory}.
   */
  protected class BufferedMOTableRow<R extends MOTableRow> implements MOTableRow<Variable> {

    private long lastRefresh;
    private R bufferedRow;
    private BufferedMOTableRow<R> nextRow;

    /**
     * Creates a new buffer row for the specified payload row. It also sets the
     * {@link #getLastRefresh()} time to the current time.
     * @param bufferedRow
     *    the buffered payload row.
     */
    public BufferedMOTableRow(R bufferedRow) {
      this.bufferedRow = bufferedRow;
      lastRefresh = System.nanoTime();
    }

    /**
     * Returns the buffered row.
     * @return
     *   the buffered row.
     */
    public R getBufferedRow() {
      return bufferedRow;
    }

    public void setBufferedRow(R bufferedRow) {
      this.bufferedRow = bufferedRow;
    }

    /**
     * Gets the immediately following row in the buffer.
     * @return
     *    the immediate successor row and {@code null} if such a row
     *    has not been buffered yet.
     */
    public BufferedMOTableRow<R> getNextRow() {
      return nextRow;
    }

    /**
     * Sets the immediately following row in the buffer.
     * @param nextRow
     *    the immediate successor row and {@code null} if such a row
     *    has not been buffered yet.
     */
    public void setNextRow(BufferedMOTableRow<R> nextRow) {
      this.nextRow = nextRow;
    }

    @Override
    public OID getIndex() {
      return (bufferedRow == null) ? null : bufferedRow.getIndex();
    }

    @Override
    public Variable getValue(int column) {
      return bufferedRow.getValue(column);
    }

    @Override
    public MOTableRow getBaseRow() {
      return bufferedRow.getBaseRow();
    }

    @Override
    public void setBaseRow(MOTableRow baseRow) {
      bufferedRow.setBaseRow(baseRow);
    }

    @Override
    public int size() {
      return bufferedRow.size();
    }

    /**
     * Returns the time when this buffered row has been refreshed from the backend last.
     * @return
     *   the last refresh time in nanoseconds.
     */
    public long getLastRefresh() {
      return lastRefresh;
    }

    /**
     * Sets the time when this buffered row has been refreshed from the backend last.
     * @param lastRefresh
     *   the last refresh time in nanoseconds.
     */
    public void setLastRefresh(long lastRefresh) {
      this.lastRefresh = lastRefresh;
    }
  }
}
