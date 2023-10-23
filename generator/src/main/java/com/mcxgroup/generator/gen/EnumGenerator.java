package com.mcxgroup.generator.gen;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import com.mcxgroup.business.enums.SeatColEnum;
import com.mcxgroup.business.enums.SeatTypeEnum;
import com.mcxgroup.business.enums.TrainTypeEnum;
import com.mcxgroup.member.enums.PassengerTypeEnum;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author johnconstantine
 */
public class EnumGenerator {
    static String path = "admin/src/assets/js/enums.js";
//    static String path = "web/src/assets/js/enums.js";

    public static void main(String[] args) {
        StringBuffer bufferObject = new StringBuffer();
        StringBuffer bufferArray = new StringBuffer();
        long begin = System.currentTimeMillis();
        try {
            toJson(PassengerTypeEnum.class, bufferObject, bufferArray);
            toJson(TrainTypeEnum.class, bufferObject, bufferArray);//生成Train的EnumsJs，存到系统里面
            toJson(SeatTypeEnum.class, bufferObject, bufferArray);//生成座位的EnumsJs，存到系统里面
            toJson(SeatColEnum.class, bufferObject, bufferArray);//生成座位的EnumsJs，存到系统里面

            StringBuffer buffer = bufferObject.append("\r\n").append(bufferArray);
            writeJs(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("执行耗时:" + (end - begin) + " 毫秒");
    }

    private static void toJson(Class clazz, StringBuffer bufferObject, StringBuffer bufferArray) throws Exception {
        // enumConst：将YesNoEnum变成YES_NO
        String enumConst = StrUtil.toUnderlineCase(clazz.getSimpleName())
                .toUpperCase().replace("_ENUM", "");
        Object[] objects = clazz.getEnumConstants();
        Method name = clazz.getMethod("name");
        Method getDesc = clazz.getMethod("getDesc");
        Method getCode = clazz.getMethod("getCode");
        //循环获取所有的Field
        ArrayList<Field> targetFields = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            System.out.println("当前遍历的 field = " + field.getName());
            // 如果字段是非私有的，或者字段的名称为"$VALUES"，则跳过当前循环，开始下一次循环
            if (!Modifier.isPrivate(field.getModifiers())||"$VALUES".equals(field.getName())){
                continue;
            }
            // 否则，将满足条件的字段添加到已创建的ArrayList中
            targetFields.add(field);
        }
        // 生成对象
        bufferObject.append(enumConst).append("={");
        for (int i = 0; i < objects.length; i++) {
            //拿到枚举的值，去拼接
            Object obj = objects[i];
            bufferObject.append(name.invoke(obj)).append(":");
            formatJsonObj(bufferObject,targetFields,clazz,obj);
            if (i < objects.length - 1) {
                bufferObject.append(",");
            }
        }
        bufferObject.append("};\r\n");

        // 生成数组
        bufferArray.append(enumConst).append("_ARRAY=[");
        for (int i = 0; i < objects.length; i++) {
            Object obj = objects[i];
            formatJsonObj(bufferArray,targetFields,clazz,obj);
            if (i < objects.length - 1) {
                bufferArray.append(",");
            }
        }
        bufferArray.append("];\r\n");
    }
    /**
     * 将一个枚举值转成JSON对象字符串
     * 比如：SeatColEnum.YDZ_A("A", "A", "1")
     * 转成：{code:"A",desc:"A",type:"1"}
     */
    private static void formatJsonObj(StringBuffer bufferObject, List<Field> targetFields, Class clazz, Object obj) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        bufferObject.append("{");
        for (int j = 0; j < targetFields.size(); j++) {
            Field field = targetFields.get(j);
            String fieldName = field.getName();
            String getMethod = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            bufferObject.append(fieldName).append(":\"").append(clazz.getMethod(getMethod).invoke(obj)).append("\"");
            if (j < targetFields.size() - 1) {
                bufferObject.append(",");
            }
        }
        bufferObject.append("}");
    }
    /**
     * 写文件
     * @param stringBuffer
     */
    public static void writeJs(StringBuffer stringBuffer) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
            System.out.println(path);
            osw.write(stringBuffer.toString());
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
