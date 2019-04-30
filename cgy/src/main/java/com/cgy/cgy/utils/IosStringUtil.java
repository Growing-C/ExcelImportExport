package com.cgy.cgy.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cgy.cgy.beans.Language;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IosStringUtil {

	public static void generateIosFile(String charSet, String fileName, List<Language> lanList) {
		if (lanList == null || lanList.size() == 0) {
			log.info("language list is null!!");
			return;
		}
		log.info("开始生成xml！fileName:{}", fileName);
		log.info("开始生成xml！fileLen:{}", lanList.size());
		File file = new File(fileName);
		try {
			ExcelUtil.createFile(file);

			FileOutputStream out = new FileOutputStream(file, true);

			int size = lanList.size();
			for (int i = 0; i < size; i++) {
				StringBuffer key = new StringBuffer();
				Language lan = lanList.get(i);
				// ios 里面引号需要加斜杠
				String line = "\"" + lan.key + "\" = \"" + lan.content.replace("\"", "\\\"") + "\";\n";
				if (lan.content.contains("\"")) {
					System.out.println("line:" + line);
				}
				key.append(line);
				out.write(key.toString().getBytes(charSet));
			}

			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("生成.文件成功！");

	}

	public static Map<String, String> readFile(String filePath, String fileEncoding) {
		File file = new File(filePath);
		if (!file.exists() || filePath == null || filePath.contains("\\.xml")) {
			log.info("fileName:{} 不存在！", filePath);
			return null;
		}

//		  Hashtable.keySet()          降序
//		  TreeMap.keySet()            升序
//		  HashMap.keySet()            乱序
//		  LinkedHashMap.keySet()      原序
		Map<String, String> map = new LinkedHashMap<>();

		FileInputStream is = null;
		try {
			if (file.length() != 0) {
				is = new FileInputStream(file);
				InputStreamReader streamReader = new InputStreamReader(is, fileEncoding);// Unicode GBK
				BufferedReader reader = new BufferedReader(streamReader);
				String line;
				while ((line = reader.readLine()) != null) {
					// stringBuilder.append(line);
//					System.out.println("read :" + line);
					Map<String, String> keyValueMap = parseLine(line);
					if (keyValueMap != null && keyValueMap.size() == 2) {
						map.put(keyValueMap.get("key"), keyValueMap.get("value"));
//						System.out.println("key :" + keyValueMap.get("key") + "  --value：" + keyValueMap.get("value"));
					}
				}
				reader.close();
				is.close();
			}

			System.out.println("map size:" + map.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public static Map<String, String> parseLine(String lineData) {
		if (lineData == null || lineData.trim().length() == 0) {
			return null;
		}
		Map<String, String> keyValueMap = new HashMap<>();
		String[] keyValueStrings = lineData.split("=");
		if (keyValueStrings == null || keyValueStrings.length != 2) {
			log.info("keyvalue Error!! {} ", keyValueStrings);
		} else {
			keyValueMap.put("key", keyValueStrings[0].trim().replaceAll("\"", ""));
			keyValueMap.put("value", keyValueStrings[1].trim().replaceAll("\"", ""));
		}

		return keyValueMap;
	}

	// 测试导入
	public static void main(String[] args) {
		String a = "adfdsaf\"f\"d";
//		System.out.println(a);
//		System.out.println(a.replace("\"", "\\\""));
//		generateIosFile("GBK", "C:/Users/chengaoyang/Desktop/lan/basic/ja/Localizable.strings",
//				ExcelUtil.importExcel("C:/Users/chengaoyang/Desktop/lan/basic/ja/", "strings.xlsx"));
//		generateIosFile("Unicode", "C:/Users/chengaoyang/Desktop/lan/basic/zh/Localizable.strings",
//				ExcelUtil.importExcel("C:/Users/chengaoyang/Desktop/lan/basic/zh/", "strings.xlsx"));
//		generateIosFile("Unicode", "C:/Users/chengaoyang/Desktop/lan/basic/en/Localizable.strings",
//				ExcelUtil.importExcel("C:/Users/chengaoyang/Desktop/lan/basic/en/", "strings.xlsx"));
//		XmlUtils.generateExcel(
//				readFile("C:/Users/chengaoyang/Desktop/lan/Language/Language/en.lproj/Localizable.strings", "Unicode"),
//				"C:/Users/chengaoyang/Desktop/lan/Language/Language/en.lproj/strings.xls");
//
//		XmlUtils.generateExcel(
//				readFile("C:/Users/chengaoyang/Desktop/lan/Language/Language/ja.lproj/Localizable.strings", "GBK"),
//				"C:/Users/chengaoyang/Desktop/lan/Language/Language/ja.lproj/strings.xls");
//
//		XmlUtils.generateExcel(
//				readFile("C:/Users/chengaoyang/Desktop/lan/Language/Language/zh-Hans.lproj/Localizable.strings",
//						"Unicode"),
//				"C:/Users/chengaoyang/Desktop/lan/Language/Language/zh-Hans.lproj/strings.xls");
//		readFile("C:/Users/chengaoyang/Desktop/lan/Language/Language/en.lproj/Localizable.strings", "Unicode");
	}

}
