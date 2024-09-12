package com.hyekyoung.batchstudy.batchfire.util;


import com.sap.conn.jco.*;
import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public final class JcoUtil {
    /**
     * JCO.Structure 객체의 값들을 해시맵(HashMap)으로 변환한다.
     *
     * @param	structure	변환할 JCO.Structure 객체
     * @return	변환된 해시맵(HashMap)
     */
    public static HashMap<String,String> toHashMap(JCoStructure structure) {
        if (structure==null) {
            return null;
        }
        HashMap <String,String>aMap = new HashMap<String,String>();
        for (JCoFieldIterator e = structure.getFieldIterator(); e.hasNextField(); ) {
            JCoField field = e.nextField();
            aMap.put(field.getName(), field.getString()); //각 필드들을 해시맵에 저장
        }
        return aMap;
    }

    /**
     * JCoTable 객체의 값들을 객체배열(ArrayList)로 변환한다.
     * JCoTable 각 행들은 HashMap객체이다.
     *
     * @param 	table	변환할 JCoTable 객체
     * @return	변환된 객체배열(ArrayList)
     */
    public static ArrayList<HashMap<String,String>> toArray(JCoTable table) {
        if (table==null) {
            return null;
        }
        ArrayList <HashMap<String,String>>arrList = new ArrayList<HashMap<String,String>>();
        HashMap <String,String>aMap = null;

        int numRows = table.getNumRows();
        for(int i=0; i<numRows; i++) {
            table.setRow(i);
            aMap = new HashMap<String,String>(); //하나의 행을 해시맵에 저장

            for (JCoFieldIterator e = table.getFieldIterator(); e.hasNextField(); ) {
                JCoField field = e.nextField();
                aMap.put(field.getName(), field.getString());
            }
            arrList.add(i, aMap); //해시맵을 객체배열에 추가
        }
        return arrList;
    }

    /**
     * JCO.Table 객체의 값들을 객체배열(ArrayList)로 변환한다.
     * JCO.Table 각 행들은 HashMap객체이다.
     *
     * @param 	table	변환할 JCO.Table 객체
     * @return	변환된 객체배열(ArrayList)
     */
    public static ArrayList<HashMap<String,String>> toCache(JCoTable table) {
        ArrayList <HashMap<String,String>>rtnObjList = new ArrayList<HashMap<String,String>>();
        if (table==null) {
            return rtnObjList;
        }
        int numRows = table.getNumRows();
        HashMap <String, String>obj = null;
        JCoField field = null;
        for(int i=0; i<numRows; i++) {
            table.setRow(i);
            obj = new HashMap<String, String>();

            for (JCoFieldIterator e = table.getFieldIterator(); e.hasNextField(); ) {
                field = e.nextField();
                obj.put(field.getName().toLowerCase(), field.getString());
            }
            rtnObjList.add(obj);
        }
        return rtnObjList;
    }
    /**
     * RFC 수행결과 반환되는 EXPORT 매개변수와 TABLE을
     * 해쉬맵 객체에 저장하여 반환한다. <br><br>
     * - 구조체(JCO.Structure)는 해쉬맵 객체안의 '해쉬맵(HashMap)'으로 저장.<br>
     * - 테이블(Table)는 해쉬맵 객체안의 '객체배열(ArrayList)'으로 저장.<br>
     * - 기타 타입은 해쉬맵 객체안의 '문자열(String)'으로 저장.<br>
     *
     * @param	outParams	RFC 수행결과 반환되는 EXPORT 매개변수와 TABLE
     * @return	변환된 해시맵(HashMap)
     */
    public static HashMap <String, Object> resultMap(JCoParameterList outParams) {
        if (outParams==null) {
            return null;
        }
        HashMap <String, Object>_map = new HashMap<String, Object>();
        ArrayList <String>keys = new ArrayList<String>();
        JCoField field = null;
        for (JCoFieldIterator e = outParams.getFieldIterator(); e.hasNextField(); ) {
            field = e.nextField();
            String name = field.getName();
            if (field.isStructure()) {
                _map.put(name, toHashMap( (JCoStructure)outParams.getStructure(field.getName()) ) );
            } else if (field.isTable()) {
                _map.put(name, toArray( (JCoTable)outParams.getTable(field.getName()) ) );
            } else {
                _map.put(name, field.getString());
            }
            keys.add(name);
        }
        if (keys.size()>0)
            _map.put("KEYS_OF_EXPORT_PARAM", keys);
        return _map;
    }

    /**
     * HashMap의 (키,값) 집합을 문자열로 반환
     *
     * @param	hashMap	문자열로 뽑아낼 해시맵객체
     * @return	(키,값) 쌍의 문자열
     */
    public static String mapString(@SuppressWarnings("rawtypes") Map hashMap) {
        if (hashMap==null)
            return "HashMap is null.";
        if ( hashMap.size()<1 )
            return "HashMap is empty.";

        StringBuffer sbuf = new StringBuffer("\n");
        sbuf.append("HashMap {\n");
        @SuppressWarnings("rawtypes")
        Iterator it = hashMap.keySet().iterator();

        for( ; it.hasNext();  ) {
            String key = (String)it.next();
            Object valObj = hashMap.get(key);
            String val = (valObj==null ? "NULL" : valObj.toString());
            sbuf.append("    ").append(key).append(" = [").append(val).append("],\n");
        }
        sbuf.append("}\n");
        return sbuf.toString();
    }

    /**
     * JCO.Table 객체로부터 칼럼(열)의 이름들을 문자열배열로 반환한다.
     *
     * @param	jcoTable	입력 JCO.Table 객체
     * @return	JCO.Table 객체의 칼럼명들(문자열배열)
     */
    public static String[] getColumnNames(JCoTable jcoTable) {
        if (jcoTable == null) {
            return null;
        }

        int colCount = jcoTable.getNumColumns();
        String[] columnNames = new String[colCount];
        int i = 0;

        for (JCoFieldIterator e = jcoTable.getFieldIterator(); e.hasNextField(); ) {
            JCoField field = e.nextField();
            columnNames[i++] = field.getName();
        }
        return columnNames;
    }
    /**
     * JCO.Table 객체로부터 칼럼(열)의 타입들을 정수배열로 반환한다.
     *
     * @param	jcoTable	입력 JCO.Table 객체
     * @return	JCO.Table 객체의 칼럼 타입들(정수배열)
     */
    public static int[] getColumnTypes(JCoTable jcoTable) {
        if (jcoTable == null) {
            return null;
        }

        int colCount = jcoTable.getNumColumns();
        int[] columnTypes = new int[colCount];
        int i = 0;

        for (JCoFieldIterator e = jcoTable.getFieldIterator(); e.hasNextField(); ) {
            JCoField field = e.nextField();
            columnTypes[i++] = field.getType();
        }
        return columnTypes;
    }
    /**
     * JCO.Table 객체로부터 칼럼(열)의 타입들을 정수배열로 반환한다.
     *
     * @param	jcoTable	입력 JCO.Table 객체
     * @return	JCO.Table 객체의 칼럼 타입들(정수배열)
     */
    public static String[] getColumnTypeNames(JCoTable jcoTable) {
        if (jcoTable == null) {
            return null;
        }

        int colCount = jcoTable.getNumColumns();
        String[] columnTypeNames = new String[colCount];
        int i = 0;

        for (JCoFieldIterator e = jcoTable.getFieldIterator(); e.hasNextField(); ) {
            JCoField field = e.nextField();
            columnTypeNames[i++] = field.getTypeAsString();
        }
        return columnTypeNames;
    }
    /**
     * JCo.ParameterList 객체로 부터 필드의 이름을 String 배열로 반환한다.
     *
     * @param   params    입력 JCo.ParameterList 객체
     * @return  JCO.ParameterList   객체의 필드명들 (String 배열)
     */
    public String[] getFieldNames(JCoParameterList params) {
        if (params == null) {
            return null;
        }

        String[] fieldNames = new String[params.getFieldCount()];
        int index = 0;

        for (JCoFieldIterator e = params.getFieldIterator(); e.hasNextField(); ) {
            JCoField field = e.nextField();
            fieldNames[index++] = field.getName();
        }

        return fieldNames;
    }
}

