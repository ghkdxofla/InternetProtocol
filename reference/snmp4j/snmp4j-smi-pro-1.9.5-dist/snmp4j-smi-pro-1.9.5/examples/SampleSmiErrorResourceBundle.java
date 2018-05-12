/*_############################################################################
  _##
  _##  SampleSmiErrorResourceBundle.java
  _##
  _##  SNMP4J-SMI-PRO
  _##  ----------------------------------
  _##  Copyright (C) 2012-2014 Frank Fock
  _##
  _##  Use of this software is subject to the license agreement you received
  _##  with this software in the "legal" folder and which can be downloaded
  _##  from http://www.snmp4j.com/smi/LICENSE.txt
  _##
  _##  Last updated: Mon Mar 17 17:00:00     2014
  _##
  _##########################################################################*/

import java.util.ListResourceBundle;
public class SampleSmiErrorResourceBundle extends ListResourceBundle {

  static final Object[][] contents = new String[][]{
      { "txtEncountered", "Encountered \"{0}\"" },
      { "txtEncounteredShort", "\"{0}\"" },
      { "txtEncounteredAsDetailText", "\nEncountered \"{0}\"" },
      { "txtLocation", "{0} at line {1}, column {2}"},
      { "txtLocationWithExpected", "{0} at line {1}, column {2}:\n{3,choice,0#|1#Was expecting:\n{4}|1<Was expecting one of:\n{4}}"},
      { "txtShort", "{0}"},
      { "txtShortWithExpected", "{4,choice,0#|1#Was expecting: {5}|1<Was expecting one of: {5}} instead {0}{1}"},
      { "txtErrNum", "[{0}]: "},
      { "errLexical", "{0}" },
      { "errParser", "Syntax error: {0}" },
      { "errParserDisplayHint", "The DISPLAY-HINT clause value {0} is invalid (RFC2579 §3.1)"},
      { "errParserUTCTime", "The UTC time value {0} does not match the mandatory format\nYYMMDDHHMMZ or YYYYMMDDHHMMZ (RFC2578 §2)"},
      { "errIdentifierTooLong", "The length of identifier {0} exceeds 64 characters (RFC2578 §3.1, §7.1.1, §7.1.4)"},
      { "errDuplicateIdentifier", "The identifier {0} is ambiguous (RFC2578 §3.1)"},
      { "errImportUnknown", "The imported MIB module {0} is unknown" },
      { "errFileOpenError", "File open error: {0}" },
      { "errImportCyclic", "Imported MIB module {0} contains a circular import" },
      { "errUndefName", "The object {0} must be defined or imported (RFC2578 §3.2)" },
      { "errUndefObject", "Unresolved object reference {0}" },
      { "errUndefSyntax", "Unresolved syntax reference {0}" },
      { "errModuleOrder", "Wrong module order within file" },
      { "errInconsistentSyntax", "The SYNTAX clause of the columnar OBJECT-TYPE definition {0}\ndoes not match with the SYNTAX clause of the corresponding SEQUENCE definition" },
      { "errInconsistentStatus", "The STATUS clause of the object definition {0} is not consistent\nwith the STATUS of the object group definition {1}" },
      { "errInconsistentTableDef", "The SEQUENCE definition for table entry {0}\ndoes not match with the number of child objects of that node" },
      { "errNotInGroup", "The object definition {0} must be included in an OBJECT-GROUP\nor a NOTIFICATION-GROUP definition respectively (RFC2580 §3.1 and §4.1)" },
      { "errWrongType", "Object reference with wrong type: {0},\nexpected type was {1}, but found {2} instead" },
      { "errUnknown", "Unknown error" },
      { "errUnknownWithMessageText", "Unknown error '{0}'" },
      { "errNoError", "" },
      { "errInvalidIndex", "The OBJECT-TYPE {0} has an invalid index definition (RFC2578 §7.7)" },
      { "errInvalidIndexLength", "The OBJECT-TYPE {0} has an invalid index definition (RFC2578 §7.7)\nbecause the minimum total index length exceeds 128 which is the maximum SNMP OID length." },
      { "errInvalidImpliedLengthIndex", "The OBJECT-TYPE {0} has an invalid index definition (RFC2578 §7.7)\nbecause the sub-index with the IMPLIED length can have a zero length." },
      { "errScalarIndex", "The OBJECT-TYPE {0} has invalid index definition,\nbecause {1} is not a columnar object (RFC2578 §7.7)" },
      { "errDuplicateRegistration", "Duplicate object registration of {0} after {1}\nfor the object ID {2} (RFC2578 §3.6)" },
      { "errIllegalRegistration", "Illegal object registration of {0} under {1}\nfor the object ID {2} (RFC2578 §7.10 et al.)" },
      { "errInconsistentAccess", "The OBJECT-TYPE {0} has inconsistent maximum access (RFC2578 §7.3)" },
      { "errWrongImport", "{0} imported from MIB module {1} must be imported from {2} instead" },
      { "errMissingImport", "Missing import statement for {0} (RFC2578 §3.2)" },
      { "errMissingIndex", "Missing INDEX clause for conceptual row definition {0} (RFC2578 §7.7)" },
      { "errInconsistentImport", "Imported object {0} is not defined in MIB module {1}" },
      { "errDuplicateImport", "Object {0} is imported twice from MIB module {1}" },
      { "errDuplicateImportSource", "MIB module {0} is imported more than once" },
      { "errIllegalImport", "{0} cannot be imported (RFC2578 §3.2)" },
      { "errNegativeIndex", "The OBJECT-TYPE {0} has invalid index definition because\n{1} may be negative (RFC2578 §7.7)" },
      { "errInconsistentTable", "The SEQUENCE clause of the table entry definition {0}\ndoes not match the order or number of objects registered for that table\nat entry {1}"},
      { "errNoAccessInGroup", "Object group {0} must not reference\nOBJECT-TYPE {1} which has a MAX-ACCESS clause\nof 'not-accessible' (RFC2580 §3.1)"},
      { "errDefaultValueOutOfRange", "The default value of OBJECT-TYPE {0} is out of range (RFC2578 §7.9)"},
      { "errDefaultValueSizeOutOfRange", "The size of the default value of OBJECT-TYPE {0}\nis out of range (RFC2578 §7.9)"},
      { "errDefaultValueInvalid", "The format of the default value of OBJECT-TYPE {0}\ndoes not match its syntax (RFC2578 §7.9)"},
      { "errDefaultValueIllegal", "A DEFVAL clause is not allowed for OBJECT-TYPE {0}\nwhich has a base syntax of Counter (Counter32 or Counter64) (RFC2578 §7.9)"},
      { "errScalarWithIndex", "OBJECT-TYPE definition {0} is a scalar and therefore\nit must not have an INDEX clause (RFC2578 §7.7)"},
      { "errInvalidSyntaxRefinement", "The syntax definition of the object {0}\nis not a valid refinement of its base syntax (RFC2578 §9)"},
      { "errIllegalClause", "The clause {0} is not allowed within this context" },
      { "errRefIsNotATable", "The object definition {0} references a {1} definition,\nexpected a reference to an OBJECT-TYPE conceptual row definition instead"},
      { "errRefIsNotAGroup", "The GROUP clause {0} references a {1} definition,\nexpected a reference to an OBJECT-GROUP or NOTIFICATION-GROUP instead (RFC2580 §5.4.2)"},
      { "errRefIsNotObjectType", "The object reference {0} points to a {1} definition,\nexpected a reference to an OBJECT-TYPE or NOTIFICATION-TYPE definition instead"},
      { "errCondGroupIsAlsoManadatory", "The conditionally GROUP clause {0}\nmust be absent from the corresponding MANDATORY-GROUPS clause (RFC2580 §5.4.2)"},
      { "errVariationNotInGroup", "OBJECT variation {0}\nmust be included in a GROUP or MANDATORY-GROUPs reference (RFC2580 §5.4.2)"},
      { "errIllegalAccessForNotifyVariation", "Only 'not-implemented' is applicable for the ACCESS clause\nof the notification type variation {0} (RFC2580 §6.5.2.3)"},
      { "errCreationRequiresNotAllowed", "The CREATION-REQUIRES clause of variation {0}\nmust only be present for conceptual row definitions (RFC2580 §6.5.2.4)"},
      { "errCreationRequiresReadCreateCols", "Only columnar object type definitions with 'read-create' access may be present\n in the CREATION REQUIRES clause of variation {0} (RFC2580 §6.5.2.4)"},
      { "errInvalidRange", "The range restriction is invalid because {0}"},
      { "errInvalidSyntax4DisplayHint", "The TEXTUAL-CONVENTION definition {0}\nmust not have a DISPLAY-HINT clause because its SYNTAX is\nOBJECT IDENTIFIER, IpAddress, Counter32, Counter64,\nor any enumerated syntax (BITS or INTEGER) (RFC2579 §3.1)"},
      { "errDisplayHintWrongType", "The DISPLAY-HINT clause value {1}\nof the TEXTUAL-CONVENTION definition {0}\nis not compatible with the used SYNTAX (RFC2579 §3.1)" },
      { "errPibIndexNotInstanceId", "The PIB-INDEX clause of OBJECT-TYPE definition {0} does not reference a columnar object with an 'InstanceId' syntax (RFC3159 §7.5)" },
      { "errPibIndexNotInstanceId", "The PIB-INDEX clause of OBJECT-TYPE definition {0} does not reference a columnar object with an 'InstanceId' syntax (RFC3159 §7.5)" },
      { "errInvalidPibTag", "The PIB-TAG clause present in {0} must be absent because the SYNTAX is not 'TagReferenceId' (RFC3159 §7.11)" },
      { "errInvalidPibReference", "The PIB-REFERENCES clause present in {0} must be absent because the SYNTAX is not 'ReferenceId' (RFC3159 §7.10)" },
      { "errMissingPibTag", "A PIB-TAG clause must be present in {0} because its SYNTAX is 'TagReferenceId' (RFC3159 §7.11)" },
      { "errMissingPibReference", "The PIB-REFERENCES must be present in {0} because its SYNTAX is 'ReferenceId' (RFC3159 §7.10)" },
      { "errUniquenessContainsPibIndex", "The UNIQUENESS clause of OBJECT-TYPE definition {0} must not contain the attribute {1} referenced in the PIB-INDEX clause (RFC3159 §7.9)" },
      { "errUniquenessDuplicateAttribute", "The UNIQUENESS clause of OBJECT-TYPE definition {0} must not contain the attribute {1} more than once (RFC3159 §7.9)" },
      { "errInstallErrorNumberOutofRange", "The INSTALL-ERRORS clause of OBJECT-TYPE definition {0} has an invalid error number\n{1} for label {2} which is out of the range 0-65535 (RFC3159 §7.4)" }
  };

  public Object[][] getContents() {
    return contents;
  }
}
