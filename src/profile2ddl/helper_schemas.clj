(ns profile2ddl.helper-schemas
  (:gen-class)
  (:require [clojure.spec.alpha :as s]))


(s/def ::xsv-type #{:unicode :float :integer})


(s/def ::redshit-integer-max 2147483647)


(s/def ::redshit-integer-min -2147483648)


(s/def ::redshift-rezerved-words #{"AES128"
                                   "AES256"
                                   "ALL"
                                   "ALLOWOVERWRITE"
                                   "ANALYSE"
                                   "ANALYZE"
                                   "AND"
                                   "ANY"
                                   "ARRAY"
                                   "AS"
                                   "ASC"
                                   "AUTHORIZATION"
                                   "BACKUP"
                                   "BETWEEN"
                                   "BINARY"
                                   "BLANKSASNULL"
                                   "BOTH"
                                   "BYTEDICT"
                                   "BZIP2"
                                   "CASE"
                                   "CAST"
                                   "CHECK"
                                   "COLLATE"
                                   "COLUMN"
                                   "CONSTRAINT"
                                   "CREATE"
                                   "CREDENTIALS"
                                   "CROSS"
                                   "CURRENT_DATE"
                                   "CURRENT_TIME"
                                   "CURRENT_TIMESTAMP"
                                   "CURRENT_USER"
                                   "CURRENT_USER_ID"
                                   "DEFAULT"
                                   "DEFERRABLE"
                                   "DEFLATE"
                                   "DEFRAG"
                                   "DELTA"
                                   "DELTA32K"
                                   "DESC"
                                   "DISABLE"
                                   "DISTINCT"
                                   "DO"
                                   "ELSE"
                                   "EMPTYASNULL"
                                   "ENABLE"
                                   "ENCODE"
                                   "ENCRYPT     "
                                   "ENCRYPTION"
                                   "END"
                                   "EXCEPT"
                                   "EXPLICIT"
                                   "FALSE"
                                   "FOR"
                                   "FOREIGN"
                                   "FREEZE"
                                   "FROM"
                                   "FULL"
                                   "GLOBALDICT256"
                                   "GLOBALDICT64K"
                                   "GRANT"
                                   "GROUP"
                                   "GZIP"
                                   "HAVING"
                                   "IDENTITY"
                                   "IGNORE"
                                   "ILIKE"
                                   "IN"
                                   "INITIALLY"
                                   "INNER"
                                   "INTERSECT"
                                   "INTO"
                                   "IS"
                                   "ISNULL"
                                   "JOIN"
                                   "LANGUAGE"
                                   "LEADING"
                                   "LEFT"
                                   "LIKE"
                                   "LIMIT"
                                   "LOCALTIME"
                                   "LOCALTIMESTAMP"
                                   "LUN"
                                   "LUNS"
                                   "LZO"
                                   "LZOP"
                                   "MINUS"
                                   "MOSTLY13"
                                   "MOSTLY32"
                                   "MOSTLY8"
                                   "NATURAL"
                                   "NEW"
                                   "NOT"
                                   "NOTNULL"
                                   "NULL"
                                   "NULLS"
                                   "OFF"
                                   "OFFLINE"
                                   "OFFSET"
                                   "OID"
                                   "OLD"
                                   "ON"
                                   "ONLY"
                                   "OPEN"
                                   "OR"
                                   "ORDER"
                                   "OUTER"
                                   "OVERLAPS"
                                   "PARALLEL"
                                   "PARTITION"
                                   "PERCENT"
                                   "PERMISSIONS"
                                   "PLACING"
                                   "PRIMARY"
                                   "RAW"
                                   "READRATIO"
                                   "RECOVER"
                                   "REFERENCES"
                                   "RESPECT"
                                   "REJECTLOG"
                                   "RESORT"
                                   "RESTORE"
                                   "RIGHT"
                                   "SELECT"
                                   "SESSION_USER"
                                   "SIMILAR"
                                   "SNAPSHOT "
                                   "SOME"
                                   "SYSDATE"
                                   "SYSTEM"
                                   "TABLE"
                                   "TAG"
                                   "TDES"
                                   "TEXT255"
                                   "TEXT32K"
                                   "THEN"
                                   "TIMESTAMP"
                                   "TO"
                                   "TOP"
                                   "TRAILING"
                                   "TRUE"
                                   "TRUNCATECOLUMNS"
                                   "UNION"
                                   "UNIQUE"
                                   "USER"
                                   "USING"
                                   "VERBOSE"
                                   "WALLET"
                                   "WHEN"
                                   "WHERE"
                                   "WITH"
                                   "WITHOUT"})