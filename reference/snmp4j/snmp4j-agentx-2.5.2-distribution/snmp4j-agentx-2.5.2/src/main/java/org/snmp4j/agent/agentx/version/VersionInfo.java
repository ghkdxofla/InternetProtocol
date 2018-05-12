/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - VersionInfo.java  
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

package org.snmp4j.agent.agentx.version;

/**
 * The <code>VersionInfo</code> object returns information about the version
 * of this SNMP4J-AgentX release.
 *
 * @author Frank Fock
 * @version 1.1
 * @since 1.1
 */
public class VersionInfo {

  public static final int MAJOR = 2;
  public static final int MINOR = 5;
  public static final int UPDATE = 2
      ;
  public static final String PATCH = "";

  public static final String VERSION =
      MAJOR + "." + MINOR + "." + UPDATE + PATCH;

  public static final int DEPENDENCY_SNMP4J_MAJOR = 2;
  public static final int DEPENDENCY_SNMP4J_MINOR = 5;
  public static final int DEPENDENCY_SNMP4J_UPDATE = 2;
  public static final String DEPENDENCY_SNMP4J_PATCH = "";

  public static final int DEPENDENCY_SNMP4J_AGENT_MAJOR = 2;
  public static final int DEPENDENCY_SNMP4J_AGENT_MINOR = 5;
  public static final int DEPENDENCY_SNMP4J_AGENT_UPDATE = 3;
  public static final String DEPENDENCY_SNMP4J_AGENT_PATCH = "";

  /**
   * Gets the version string for this release.
   * @return
   *    a string of the form <code>major.minor.update[patch]</code>.
   */
  public static String getVersion() {
    return VERSION;
  }

  /**
   * Checks whether SNMP4J has the minimum required version.
   * @return
   *    <code>true</code> if the dependencies have the minimum required
   *    version(s).
   */
  public static boolean checkMinVersionOfDependencies() {
    return org.snmp4j.version.VersionInfo.MAJOR >= DEPENDENCY_SNMP4J_MAJOR
        && org.snmp4j.version.VersionInfo.MINOR >= DEPENDENCY_SNMP4J_MINOR
        && org.snmp4j.version.VersionInfo.UPDATE >= DEPENDENCY_SNMP4J_UPDATE
        && org.snmp4j.version.VersionInfo.PATCH.compareTo(DEPENDENCY_SNMP4J_PATCH)>=0
        && org.snmp4j.version.VersionInfo.MAJOR >= DEPENDENCY_SNMP4J_AGENT_MAJOR
        && org.snmp4j.version.VersionInfo.MINOR >= DEPENDENCY_SNMP4J_AGENT_MINOR
        && org.snmp4j.version.VersionInfo.UPDATE >= DEPENDENCY_SNMP4J_AGENT_UPDATE
        && org.snmp4j.version.VersionInfo.PATCH.compareTo(DEPENDENCY_SNMP4J_AGENT_PATCH)>=0;
  }

  private VersionInfo() {
  }

}
