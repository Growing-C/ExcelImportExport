package com.cgy.cgy.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgy.cgy.beans.Resident;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XmlUtils {

	private static Logger log = LoggerFactory.getLogger(ExcelUtil.class);

	public static Map<String, String> readXml(String filePath) {
		File file = new File(filePath);
		if (!file.exists() || filePath == null || filePath.contains("\\.xml")) {
			log.info("fileName:{} 不存在！", filePath);
			return null;
		}

		SAXReader reader = new SAXReader(); // User.hbm.xml表示你要解析的xml文档

		Map<String, String> map = new HashMap<>();
		Document doc = null;
		try {
			// 将字符串转为XML
			doc = reader.read(file);
//			doc = DocumentHelper.parseText(xml);
			// 获取根节点
			Element rootElt = doc.getRootElement();
			// 拿到根节点的名称
			System.out.println("根节点：" + rootElt.getName());

			// 获取根节点下的子节点head
			Iterator iter = rootElt.elementIterator("string");
			// 遍历head节点
			while (iter.hasNext()) {
				Element recordEle = (Element) iter.next();
				// 拿到head节点下的子节点title值
				String key = recordEle.attributeValue("name");
				if (key == null || key.length() == 0) {
					log.info(" {} 不存在！", "该string 的key");
					continue;
				}
				String value = recordEle.getStringValue();

				map.put(key, value);
//				System.out.println("key:" + key + "   String value:" + value);
			}

			log.info(" map大小为 {}！", map.size());
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public static void generateExcel(Map<String, String> map, String fileName) {
		if (map == null || map.size() == 0) {
			log.info("map is null");
			return;
		}
		System.out.println("------------------start create excel-------------------------------");
//		Workbook[] wbs = new Workbook[] { new HSSFWorkbook(), new XSSFWorkbook() };
		Workbook[] wbs = new Workbook[] { new XSSFWorkbook() };
		for (int i = 0; i < wbs.length; i++) {
			Workbook workbook = wbs[i];
			// 得到一个POI的工具类
			CreationHelper createHelper = workbook.getCreationHelper();

			// 在Excel工作簿中建一工作表，其名为缺省值, 也可以指定Sheet名称
			Sheet sheet = workbook.createSheet();
			// Sheet sheet = workbook.createSheet("SheetName");

			// 用于格式化单元格的数据
			DataFormat format = workbook.createDataFormat();

			// 设置字体
			Font font = workbook.createFont();
			font.setFontHeightInPoints((short) 15); // 字体高度
			font.setColor(Font.COLOR_RED); // 字体颜色
			font.setFontName("黑体"); // 字体
//			font.setBoldweight(Font.BOLDWEIGHT_BOLD); // 宽度
			font.setItalic(true); // 是否使用斜体
			// font.setStrikeout(true); //是否使用划线

			CellStyle titleStyle = workbook.createCellStyle();
			titleStyle.setFont(font);
			titleStyle.setAlignment(HorizontalAlignment.LEFT); // 水平布局：居中
			titleStyle.setWrapText(true);

			// 设置单元格类型
			CellStyle cellStyle = workbook.createCellStyle();
//			cellStyle.setFont(font);
//			cellStyle.setAlignment(CellStyle.ALIGN_CENTER); // 水平布局：居中
			cellStyle.setWrapText(true);
//

			Row headerRow = sheet.createRow(0);
			// 创建单元格
			Cell titleKeyCell = headerRow.createCell(0);
			titleKeyCell.setCellValue("android key");// 设置单元格内容
			titleKeyCell.setCellStyle(titleStyle);// 设置单元格样式

			Cell titleValueCell = headerRow.createCell(1);
			titleValueCell.setCellValue("value");// 设置单元格内容
			titleValueCell.setCellStyle(titleStyle);// 设置单元格样式

			Set<String> keys = map.keySet();
			Iterator<String> it = keys.iterator();
			int rowNum = 1;// 从1开始，空一行
			System.out.println("input map size:" + map.size());
			while (it.hasNext()) {
				String key = it.next();
				String value = map.get(key);

				// 创建行
				Row row = sheet.createRow(rowNum);
				rowNum++;

				// 创建单元格
				Cell keyCell = row.createCell(0);
				keyCell.setCellValue(key);// 设置单元格内容
				keyCell.setCellStyle(cellStyle);// 设置单元格样式

				Cell valueCell = row.createCell(1);
				valueCell.setCellValue(value);// 设置单元格内容
				valueCell.setCellStyle(cellStyle);// 设置单元格样式

			}
			System.out.println("rowNum:" + rowNum);
			sheet.autoSizeColumn(0); // 调整第一列宽度
			sheet.autoSizeColumn(1); // 调整第一列宽度

			String filename = fileName;
			if (workbook instanceof XSSFWorkbook) {
				filename = filename + "x";
			}
			File file = new File(filename);
			if (file.exists())
				file.delete();

			try {
				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			System.out.println("exist:" + file.exists());

			FileOutputStream out;
			try {
				out = new FileOutputStream(filename);
				workbook.write(out);
				out.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("------------------end create excel-------------------------------");

	}

	// 测试导入
	public static void main(String[] args) {
		generateExcel(readXml("C:/Users/chengaoyang/Desktop/lan/strings.xml"),
				"C:/Users/chengaoyang/Desktop/lan/strings.xls");
	}

}
