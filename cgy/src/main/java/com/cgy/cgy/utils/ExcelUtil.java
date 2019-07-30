package com.cgy.cgy.utils;

import static org.apache.poi.ss.usermodel.CellType.BOOLEAN;
import static org.apache.poi.ss.usermodel.CellType.ERROR;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;

import com.cgy.cgy.beans.Language;
import com.cgy.cgy.beans.Resident;
import com.microsoft.schemas.office.visio.x2012.main.CellType;

public class ExcelUtil {

	private static Logger log = LoggerFactory.getLogger(ExcelUtil.class);

	/**
	 * key-value模式的excel 表生成 .xml
	 * 
	 * @param folderName
	 * @param fileN
	 * @param startLine  开始行 0表示从第一行开始，1表示 从第二行开始（跳过第一行）
	 * @return
	 */
	public static List<Language> importExcelKeyValue(String folderName, String fileN, int startLine) {
		String fileName = folderName + fileN;
		if (fileN == null || !fileN.contains(".xls")) {
			log.info("fileName:{} 不是excel，不解析", fileName);
			return null;
		}
		log.info("导入excel解析开始，fileName:{}", fileName);
		try {

			List<Language> list = new ArrayList<>();
			InputStream inputStream = new FileInputStream(fileName);
			Workbook workbook = WorkbookFactory.create(inputStream);
			Sheet sheet = workbook.getSheetAt(0);
			// 获取sheet的行数
			int rows = sheet.getPhysicalNumberOfRows();
			A: for (int i = 0; i < rows; i++) {
				// 过滤表头行
				if (i < startLine) {
					continue;
				}
				// 获取当前行的数据
				Row row = sheet.getRow(i);
				Language lan = new Language();

				if (row.getPhysicalNumberOfCells() != 2) {
					log.info(" 行号:" + (row.getRowNum() + 1) + " 有内容的cell数目为：" + row.getPhysicalNumberOfCells()
							+ "，不符合要求，剔除");
					continue;
				}
				for (Cell cell : row) {
					int column = cell.getColumnIndex();
					String cellText = cell.getStringCellValue();

					if (column == 0) {
						lan.key = cellText;
						if (cellText == null || cellText.length() == 0) {
							throw new NullPointerException("cell key is null!");
						}
					} else {
						lan.content = cellText;

						if (cellText == null || cellText.length() == 0) {
							log.info("cell value is null skip this line key:" + lan.key + " " + lan.content);
							continue A;
						}
					}
				}

//				log.info("add key:" + lan.key + " value:" + lan.content);
				list.add(lan);
			}
			log.info("导入文件解析成功！总数：" + list.size());

//			genenrateXml(list, folderName + fileName.split("\\.")[1] + "/", "net_errors.xml");
			return list;
		} catch (Exception e) {
			log.info("导入文件解析失败！");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * a 生成 语言xml, 是一个string <string name="tab_lock">LOCK</string>
	 * 
	 * @param list
	 * @param fileName 目标文件名称
	 */
	public static void genenrateXmlString(List<Language> list, String folderPath, String fileN) {
		if (list == null || list.size() == 0)
			return;
		try {

			String fileName = folderPath + fileN;
			log.info("开始生成xml！fileName:{}", fileName);
			File file = new File(fileName);
			createFile(file);

			FileOutputStream out = new FileOutputStream(file, true);
			StringBuffer topHead = new StringBuffer();
			topHead.append("<resources>\n");
			out.write(topHead.toString().getBytes("utf-8"));

			int size = list.size();
			for (int i = 0; i < size; i++) {
				StringBuffer key = new StringBuffer();
//			    <string name="tab_lock">LOCK</string>
				Language la = list.get(i);
				key.append("      <string name=\"" + la.key + "\">" + la.content + "</string>\n");
				out.write(key.toString().getBytes("utf-8"));
			}
			StringBuffer bottom = new StringBuffer();
			bottom.append("</resources>");
			out.write(bottom.toString().getBytes("utf-8"));

			out.close();

			log.info("生成xml成功！");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 生成 语言xml,是一个string - array
	 * 
	 * @param list
	 * @param fileName 目标文件名称
	 */
	public static void genenrateXmlStringArray(List<Language> list, String folderName, String fileN) {
		if (list == null || list.size() == 0)
			return;
		try {

			String fileName = folderName + fileN;
			log.info("开始生成xml！fileName:{}", fileName);
			File file = new File(fileName);
			createFile(file);

			FileOutputStream out = new FileOutputStream(file, true);
			StringBuffer topHead = new StringBuffer();
			topHead.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
					+ "<resources>\n    <string-array name=\"net_error_code\">\n");
			out.write(topHead.toString().getBytes("utf-8"));

			int size = list.size();
			for (int i = 0; i < size; i++) {
				StringBuffer key = new StringBuffer();
				key.append("        <item>" + list.get(i).key + "</item>\n");
				out.write(key.toString().getBytes("utf-8"));
			}
			StringBuffer center = new StringBuffer();
			center.append("    </string-array>\r\n" + "    <string-array name=\"net_error_content\">\n");
			out.write(center.toString().getBytes("utf-8"));

			for (int i = 0; i < size; i++) {
				StringBuffer content = new StringBuffer();
				content.append("        <item>" + list.get(i).content + "</item><!--" + list.get(i).key + "-->\n");
				out.write(content.toString().getBytes("utf-8"));
			}

			StringBuffer bottom = new StringBuffer();
			bottom.append("    </string-array>\r\n" + "</resources>");
			out.write(bottom.toString().getBytes("utf-8"));

			out.close();

			log.info("生成xml成功！");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createFile(File file) throws IOException {
		if (file == null)
			return;
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (file.exists())
			file.delete();
		file.createNewFile();
	}

	// 测试导入
	public static void main(String[] args) {
		batchGenerateLanguageXmlStringOrArray("C:/Users/chengaoyang/Desktop/sources/bclbasic/ErrorCode/", true);
	}

	/**
	 * 根据 excel生成xml用于 语言
	 */
	public static void readExcelAndGenerateXmlString(File srcFile, String xmlName, boolean isArray) {
//		importExcelKeyValue("C:/Users/chengaoyang/Desktop/", "zh.xls", 1);
		List<Language> list = importExcelKeyValue(srcFile.getParent() + "\\", srcFile.getName(), 0);
		String xmlFolderName = srcFile.getParent() + "\\android\\";
		if (srcFile.getName().contains("ja")) {
			xmlFolderName = xmlFolderName + "ja\\";
		} else if (srcFile.getName().contains("zh")) {
			xmlFolderName = xmlFolderName + "zh\\";
		} else if (srcFile.getName().contains("en")) {
			xmlFolderName = xmlFolderName + "en\\";
		} else {
			xmlFolderName = xmlFolderName + "error\\";
		}

		log.info(" readExcelAndGenerateXmlString！fileName:{}", xmlFolderName);
		if (isArray) {
			genenrateXmlStringArray(list, xmlFolderName, xmlName);
		} else
			genenrateXmlString(list, xmlFolderName, xmlName);
	}

	/**
	 * a 批量生成 string.xml或者string-array
	 */
	public static void batchGenerateLanguageXmlStringOrArray(String folderPath, boolean isArray) {
		File file = new File(folderPath);
		if (file.exists() && file.isDirectory()) {
			File[] xlsFiles = file.listFiles();
			if (xlsFiles != null && xlsFiles.length > 0) {
				for (File f : xlsFiles) {
					readExcelAndGenerateXmlString(f, "net_errors.xml", isArray);
				}
			}
		}

	}

}

//System.out.println("getCellType-->"+cell.getCellType());
// 获取值并自己格式化
//switch (cell.getCellType()) {
//case Cell.CELL_TYPE_STRING:// 字符串型
//	String cellContent = cell.getRichStringCellValue().getString();
//	System.out.print("cell length:" + cellContent.length() + "---");
//	System.out.println(cell.getRichStringCellValue().getString());
//	break;
//case Cell.CELL_TYPE_NUMERIC:// 数值型
//	if (DateUtil.isCellDateFormatted(cell)) { // 如果是date类型则 ，获取该cell的date值
//		System.out.println(cell.getDateCellValue());
//	} else {// 纯数字
//		System.out.println(cell.getNumericCellValue());
//	}
//	break;
//case Cell.CELL_TYPE_BOOLEAN:// 布尔
//	System.out.println(cell.getBooleanCellValue());
//	break;
//case Cell.CELL_TYPE_FORMULA:// 公式型
//	System.out.println(cell.getCellFormula());
//	break;
//case Cell.CELL_TYPE_BLANK:// 空值
//	System.out.println("empty!");
//	break;
//case Cell.CELL_TYPE_ERROR: // 故障
//	System.out.println("error!");
//	break;
//default:
//	System.out.println("default-->" + cell.getCellType());
//}

//public static void exportTemplate() throws IOException {
//	// 声明一个工作薄
//	HSSFWorkbook workbook = new HSSFWorkbook();
//	// 创建一个Excel表单,参数为sheet的名字
//	HSSFSheet sheet = workbook.createSheet("模板表");
//	// 创建表头
//	setTitle(workbook, sheet);
//	List<Map<String, Object>> oalist = budgetAdjustService.getOainform(oaId);
//	// 新增数据行，并且设置单元格数据
//	HSSFRow hssfRow = sheet.createRow(1);
//	for (Map map : oalist) {
//		hssfRow.createCell(0).setCellValue(map.get("adjustType") + "");
//		hssfRow.createCell(1).setCellValue(map.get("applyDate") + "");
//		hssfRow.createCell(2).setCellValue(map.get("processCode") + "");
//		hssfRow.createCell(3).setCellValue(map.get("applyOrganization") + "");
//		hssfRow.createCell(4).setCellValue(map.get("applyDepartment") + "");
//		hssfRow.createCell(5).setCellValue(map.get("flag") + "");
//	}
//	hssfRow.createCell(6).setCellValue(adjOrg);
//	hssfRow.createCell(7).setCellValue(adjDepart);
//	hssfRow.createCell(8).setCellValue(adjSubject);

// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
//	String fileName = "Template -" + new Date().getTime() + ".xls";
//	// 清空response
//	response.reset();
//	// 设置response的Header
//	response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
//	OutputStream os = new BufferedOutputStream(response.getOutputStream());
//	response.setContentType("application/vnd.ms-excel;charset=gb2312");
//	// 将excel写入到输出流中
//	workbook.write(os);
//	os.flush();
//	os.close();
//}
