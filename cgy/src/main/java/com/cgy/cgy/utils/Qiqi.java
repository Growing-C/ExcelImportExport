package com.cgy.cgy.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgy.cgy.beans.Resident;

public class Qiqi {

	private static Logger log = LoggerFactory.getLogger(ExcelUtil.class);

	// 测试导入
	public static void main(String[] args) {
		try {
			List<Map.Entry<String, HashMap<String, Resident>>> all = readExcel(
					"C:/Users/chengaoyang/Desktop/qiqi/数据导入.xls");
//			createExcel(all, "C:/Users/chengaoyang/Desktop/qiqi/aaaa.xls");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 数据导入--》身份确认表格
	public static List<Map.Entry<String, HashMap<String, Resident>>> readExcel(String fileName)
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		InputStream inputStream = new FileInputStream(fileName);
		System.out.println("------------------start read-------------------------------");
		Workbook workbook = WorkbookFactory.create(inputStream);
		Sheet sheet = workbook.getSheetAt(0);

		HashMap<String, HashMap<String, Resident>> allResidentsMap = new HashMap<>();

		HashMap<String, Resident> currentFamilyMap = new HashMap<>(); // 当前家庭

		DataFormatter formatter = new DataFormatter();
		int titleRowCount = 2;// 标题有二行
//		String currentFamilyTag = "";// 用来分户
		int totalNum = 0;
		for (Row row : sheet) {
			if (titleRowCount > 0) {// 去掉标题行
				titleRowCount--;
				continue;
			}

			Resident resident = new Resident();
			for (Cell cell : row) {

				// 通过获取单元格值并应用任何数据格式（Date，0.00，1.23e9，$ 1.23等），获取单元格中显示的文本
				String cellText = formatter.formatCellValue(cell);
				cellText = cell.getRichStringCellValue().getString();
//				System.out.println(cellText);
				switch (cell.getColumnIndex()) {

				case 0:// 身份证号(必填)
					resident.idNum = cellText;
					break;
				case 1:// 姓名 (必填)
					resident.name = cellText;
					break;
				case 2:// 性别
					resident.gender = cellText;
					break;
				case 3:// 年龄
					resident.age = cellText;
					break;
				case 4:// 家庭地址
					resident.address = cellText;
					break;
				case 5:// 联系电话
					resident.mobile = cellText;
					break;
				case 6:// 家庭人口
					break;
				case 7:// 所在区县(必填)
					break;
				case 8:// 所在镇(街道)(必填)
					break;
				case 9:// 所在村(必填)
					break;
				case 10:// 所属户编号(必填)
					resident.residenceNum = cellText;
					break;
				case 11:// 与户主的关系(必填)
					resident.relationship = cellText;
					break;
				case 12:// 文化程度
					break;
				case 13:// 人员状态
					break;
				default:
					System.out.println("error!!! not import this cell");
					break;
				}
			}
//			System.out.println("resident:" + resident);
			if (!allResidentsMap.containsKey(resident.residenceNum)) {// 不是同一户人，就用新的
				currentFamilyMap = new HashMap<>();

				allResidentsMap.put(resident.residenceNum, currentFamilyMap);
			} else {
				if (resident.residenceNum == null || resident.residenceNum.length() == 0) {
					throw new NullPointerException("  residenceNum 不能为空:" + resident);
				}
				currentFamilyMap = allResidentsMap.get(resident.residenceNum);
			}
			if (resident.relationship != null && resident.relationship.contains("户主")) {
				if (currentFamilyMap.containsKey("户主")) {
					currentFamilyMap.put("户主2", resident);
					System.out.println("  已经有户主了！:" + currentFamilyMap.get("户主"));
//					throw new IllegalArgumentException("  已经有户主了！:" + currentFamilyMap.get("户主") + "\n" + resident);
				} else {
					currentFamilyMap.put("户主", resident);
				}
//				System.out.println("我是户主！");
			} else {
				if (currentFamilyMap.containsKey(resident.idNum)) {
					throw new IllegalArgumentException("已经有这个用户了！:" + resident);
				} else if (resident.idNum == null || resident.idNum.length() == 0) {
					throw new NullPointerException("  idNum 不能为空:" + resident);
				}
//				resident.relationship =resident.relationship;
				currentFamilyMap.put(resident.idNum, resident);
			}
			totalNum++;
		}
//		27组22号
		List<Map.Entry<String, HashMap<String, Resident>>> list = new ArrayList<Map.Entry<String, HashMap<String, Resident>>>(
				allResidentsMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, HashMap<String, Resident>>>() {
			// 升序排序
			public int compare(Entry<String, HashMap<String, Resident>> o1,
					Entry<String, HashMap<String, Resident>> o2) {
				String re1Add = o1.getValue().get("户主").address;
				String re2Add = o2.getValue().get("户主").address;
				if (re1Add.length() == 0 || re2Add.length() == 0) {
					throw new NullPointerException("地址为空！排序无法进行");
				}
				String[] addStrs1 = re1Add.split("组");
				String[] addStrs2 = re2Add.split("组");
				int zu1 = Integer.parseInt(addStrs1[0]);
				int zu2 = Integer.parseInt(addStrs2[0]);
				if (zu1 < zu2) {
					return -1;
				} else if (zu1 > zu2) {
					return 1;
				} else {
					int hao1 = Integer.parseInt(addStrs1[1].replace("号", "").split("-")[0]);
					int hao2 = Integer.parseInt(addStrs2[1].replace("号", "").split("-")[0]);
					return hao1 - hao2;
				}
			}

		});

//		for (Map.Entry<String, HashMap<String, Resident>> mapping : list) {
//			System.out.println(mapping.getKey() + ":" + mapping.getValue().get("户主"));
//		}
		System.out.println("总条数 totalNum:" + totalNum);
		System.out.println("allResidentsMap size:" + list.size());
		System.out.println("------------------end read-------------------------------");
		return list;
	}

	// 创建表头
	private static void setTitle(Workbook workbook, Sheet sheet, int titleRow) {
		Row row = sheet.createRow(titleRow);
		// 设置列宽，setColumnWidth的第二个参数要乘以256，这个参数的单位是1/256个字符宽度
		sheet.setColumnWidth(8, 60 * 256);

		// 设置字体
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 20); // 字体高度
		font.setColor(Font.COLOR_RED); // 字体颜色
		font.setFontName("黑体"); // 字体
		font.setItalic(true); // 是否使用斜体

		// 设置单元格类型
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		cellStyle.setFont(font);

		// 导出的Excel头部
		String[] headers = { "序号", "户主", "与户主关系", "姓名", "性别", "身份证号码", "户籍地址", "联系电话", "签名", "备注" };
		// 设置表格默认列宽度为15个字节
		sheet.setDefaultColumnWidth((short) 16);
		CreationHelper createHelper = workbook.getCreationHelper();
		for (short i = 0; i < headers.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(createHelper.createRichTextString(headers[i]));// 设置单元格内容
//			cell.setCellValue(headers[i]);// 设置单元格内容
			cell.setCellStyle(cellStyle);// 设置单元格样式

		}
	}

	public static void createExcel(List<Map.Entry<String, HashMap<String, Resident>>> allResidents, String fileName)
			throws IOException {

		System.out.println("------------------start create excel-------------------------------");
		Workbook[] wbs = new Workbook[] { new XSSFWorkbook() };
		Workbook workbook = wbs[0];
		// 得到一个POI的工具类
		CreationHelper createHelper = workbook.getCreationHelper();

		// 在Excel工作簿中建一工作表，其名为缺省值, 也可以指定Sheet名称
		Sheet sheet = workbook.createSheet();

		// 用于格式化单元格的数据
		DataFormat format = workbook.createDataFormat();

		// 设置字体
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 20); // 字体高度
		font.setColor(Font.COLOR_RED); // 字体颜色
		font.setFontName("黑体"); // 字体
		font.setItalic(true); // 是否使用斜体

		// 设置单元格类型
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);

		Iterator<Map.Entry<String, HashMap<String, Resident>>> it = allResidents.iterator();
		int rowNum = 2;// 从2开始
		setTitle(workbook, sheet, rowNum);
		rowNum++;
		int size = 0;
		System.out.println("input map size:" + allResidents.size());
		while (it.hasNext()) {
			HashMap<String, Resident> familyMap = it.next().getValue();
			if (familyMap == null || familyMap.size() == 0) {
				throw new IOException("error no this family");
			}
			Resident owner = familyMap.get("户主");

			size += familyMap.size();

			Iterator<Resident> family = familyMap.values().iterator();
			while (family.hasNext()) {
				Resident resi = family.next();

				if (owner == null) {
					System.out.println("没有户主--》" + resi);
					while (family.hasNext()) {
						System.out.println("------》" + family.next());
					}
					return;
				}
				// 创建行
				Row row = sheet.createRow(rowNum);

//					"序号", "户主", "与户主关系", "姓名", "性别", "身份证号码", "户籍地址", "联系电话", "签名", "备注" 
				for (int column = 0; column < 10; column++) {
					String cellValue = "";

					switch (column) {
					case 0:// 序证号
						cellValue = String.valueOf(rowNum - 2);
						break;
					case 1:// 户主
						cellValue = owner.name;
						break;
					case 2:// 与户主关系
						cellValue = resi.relationship;
						break;
					case 3:// 姓名
						cellValue = resi.name;
						break;
					case 4:// 性别
						cellValue = resi.gender;
						break;
					case 5:// 身份证号码
						cellValue = resi.idNum;
						break;
					case 6:// 户籍地址
						cellValue = resi.address;
						break;
					case 7:// 联系电话
						cellValue = resi.mobile;
						break;
					case 8:// 签名
						cellValue = "";
						break;
					case 9:// 备注
						cellValue = "";
						break;
					default:
						System.out.println("error!!! not import this cell");
						break;
					}

					// 创建单元格
					Cell cell = row.createCell(column);
					cell.setCellValue(cellValue);// 设置单元格内容
					cell.setCellStyle(cellStyle);// 设置单元格样式

				}

				rowNum++;
			}
//				System.out.println("rowNum:" + rowNum);
		}
		System.out.println("rowNum:" + rowNum);
		System.out.println("total write size:" + size);
		sheet.autoSizeColumn(0); // 调整第一列宽度
		sheet.autoSizeColumn(1); // 调整第一列宽度
		sheet.autoSizeColumn(2); // 调整第一列宽度
		sheet.autoSizeColumn(3); // 调整第一列宽度
		sheet.autoSizeColumn(4); // 调整第一列宽度
		sheet.autoSizeColumn(5); // 调整第一列宽度
		sheet.autoSizeColumn(6); // 调整第一列宽度
		sheet.autoSizeColumn(7); // 调整第一列宽度
		sheet.autoSizeColumn(8); // 调整第一列宽度
		sheet.autoSizeColumn(9); // 调整第一列宽度
		// 保存
		String filename = fileName;
		if (workbook instanceof XSSFWorkbook) {
			filename = filename + "x";
		}

		FileOutputStream out = new FileOutputStream(filename);
		workbook.write(out);
		out.close();

		System.out.println("------------------end create excel-------------------------------");
	}
}
