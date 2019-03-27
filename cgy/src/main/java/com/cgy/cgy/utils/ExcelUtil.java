package com.cgy.cgy.utils;

import static org.apache.poi.ss.usermodel.CellType.BOOLEAN;
import static org.apache.poi.ss.usermodel.CellType.ERROR;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

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

import com.cgy.cgy.beans.Resident;
import com.microsoft.schemas.office.visio.x2012.main.CellType;

public class ExcelUtil {

	private static Logger log = LoggerFactory.getLogger(ExcelUtil.class);

	/**
	 * 根据身份证号获取年龄
	 * 
	 * @param id
	 * @return
	 */
	public static int getAgeFromIdNum(String id) {
		if (id == null || id.length() == 0) {
			return 0;
		}
		int leh = id.length();
		String dates = "";
		if (leh == 18) {
//			int se = Integer.valueOf(id.substring(leh - 1)) % 2;
			dates = id.substring(6, 10);
			SimpleDateFormat df = new SimpleDateFormat("yyyy");
			String year = df.format(new Date());
			int u = Integer.parseInt(year) - Integer.parseInt(dates);
			return u;
		} else {
			dates = id.substring(6, 8);
			return Integer.parseInt(dates);
		}

	}

	public static void createExcel(HashMap<String, HashMap<String, Resident>> allResidents, String fileName)
			throws IOException {

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
			font.setFontHeightInPoints((short) 20); // 字体高度
			font.setColor(Font.COLOR_RED); // 字体颜色
			font.setFontName("黑体"); // 字体
//			font.setBoldweight(Font.BOLDWEIGHT_BOLD); // 宽度
			font.setItalic(true); // 是否使用斜体
			// font.setStrikeout(true); //是否使用划线

			// 设置单元格类型
			CellStyle cellStyle = workbook.createCellStyle();
//			cellStyle.setFont(font);
//			cellStyle.setAlignment(CellStyle.ALIGN_CENTER); // 水平布局：居中
			cellStyle.setWrapText(true);
//
//			CellStyle cellStyle2 = workbook.createCellStyle();
//			cellStyle2.setDataFormat(format.getFormat("＃, ## 0.0"));
//
//			CellStyle cellStyle3 = workbook.createCellStyle();
//			cellStyle3.setDataFormat(format.getFormat("yyyy-MM-dd HH:mm:ss"));

			// 添加单元格注释
			// 创建Drawing对象,Drawing是所有注释的容器.
//			Drawing drawing = sheet.createDrawingPatriarch();
			// ClientAnchor是附属在WorkSheet上的一个对象， 其固定在一个单元格的左上角和右下角.
//			ClientAnchor anchor = createHelper.createClientAnchor();
			// 设置注释位子
//			anchor.setRow1(0);
//			anchor.setRow2(2);
//			anchor.setCol1(0);
//			anchor.setCol2(2);
			// 定义注释的大小和位置,详见文档
//			Comment comment = drawing.createCellComment(anchor);
			// 设置注释内容
//			RichTextString str = createHelper.createRichTextString("Hello, World!");
//			comment.setString(str);
			// 设置注释作者. 当鼠标移动到单元格上是可以在状态栏中看到该内容.
//			comment.setAuthor("H__D");
			Set<String> keys = allResidents.keySet();
			Iterator<String> it = keys.iterator();
			int rowNum = 1;// 从1开始，空一行
			int size = 0;
			System.out.println("input map size:" + allResidents.size());
			while (it.hasNext()) {
				HashMap<String, Resident> familyMap = allResidents.get(it.next());
				if (familyMap == null || familyMap.size() == 0) {
					throw new IOException("error no this resident");
				}
				Resident owner = familyMap.get("户主");
//				if (owner == null) {
//					System.out.println("没有户主--》" + owner);
//					return;
//				}

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
					rowNum++;
//					身份证号(必填)	姓名　(必填)	性别	年龄	家庭地址	联系电话	家庭人口	所在区县(必填)	所在镇(街道)(必填)	所在村(必填)	所属户编号(必填)	与户主的关系(必填)	文化程度	人员状态

					for (int column = 0; column < 14; column++) {
						String cellValue = "";

						switch (column) {
						case 0:// 身份证号
							cellValue = resi.idNum;
							break;
						case 1:// 姓名
							cellValue = resi.name;
							break;
						case 2:// 性别
							cellValue = resi.gender;
							break;
						case 3:// 年龄
							cellValue = String.valueOf(getAgeFromIdNum(resi.idNum));
							break;
						case 4:// 家庭地址
							cellValue = resi.address;
							break;
						case 5:// 联系电话
							cellValue = resi.contactPhone;
							break;
						case 6:// 家庭人口
							cellValue = String.valueOf(familyMap.size());
							break;
						case 7:// 所在区县(必填)
							cellValue = "通州区";
							break;
						case 8:// 所在镇(街道)(必填)
							cellValue = "刘桥镇";
							break;
						case 9:// 所在村(必填)
							cellValue = "蒋一村";
							break;
						case 10:// 所属户编号(必填)
							cellValue = owner.idNum;
							break;
						case 11:// 与户主的关系(必填)
							cellValue = resi.relationship;
							break;
						case 12:// 文化程度

							break;
						case 13:// 人员状态
							cellValue = "正常";
							break;
						default:
							System.out.println("error!!! not import this cell");
							break;
						}

						// 创建单元格
						Cell cell = row.createCell(column);
//						cell.setCellValue(createHelper.createRichTextString("Hello！" + rownum));// 设置单元格内容
						cell.setCellValue(cellValue);// 设置单元格内容
						cell.setCellStyle(cellStyle);// 设置单元格样式
//						cell.setCellType(Cell.CELL_TYPE_STRING);// 指定单元格格式：数值、公式或字符串
//						cell.setCellComment(comment);// 添加注释

//						sheet.autoSizeColumn(column); // 调整第一列宽度
					}

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
			sheet.autoSizeColumn(10); // 调整第一列宽度
			sheet.autoSizeColumn(11); // 调整第一列宽度
			sheet.autoSizeColumn(12); // 调整第一列宽度
			sheet.autoSizeColumn(13); // 调整第一列宽度
			// 保存
			String filename = fileName;
			if (workbook instanceof XSSFWorkbook) {
				filename = filename + "x";
			}

			FileOutputStream out = new FileOutputStream(filename);
			workbook.write(out);
			out.close();
		}

		System.out.println("------------------end create excel-------------------------------");
	}

	public static void exportTemplate() throws IOException {
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 创建一个Excel表单,参数为sheet的名字
		HSSFSheet sheet = workbook.createSheet("模板表");
		// 创建表头
		setTitle(workbook, sheet);
//		List<Map<String, Object>> oalist = budgetAdjustService.getOainform(oaId);
//		// 新增数据行，并且设置单元格数据
//		HSSFRow hssfRow = sheet.createRow(1);
//		for (Map map : oalist) {
//			hssfRow.createCell(0).setCellValue(map.get("adjustType") + "");
//			hssfRow.createCell(1).setCellValue(map.get("applyDate") + "");
//			hssfRow.createCell(2).setCellValue(map.get("processCode") + "");
//			hssfRow.createCell(3).setCellValue(map.get("applyOrganization") + "");
//			hssfRow.createCell(4).setCellValue(map.get("applyDepartment") + "");
//			hssfRow.createCell(5).setCellValue(map.get("flag") + "");
//		}
//		hssfRow.createCell(6).setCellValue(adjOrg);
//		hssfRow.createCell(7).setCellValue(adjDepart);
//		hssfRow.createCell(8).setCellValue(adjSubject);

		// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
//		String fileName = "Template -" + new Date().getTime() + ".xls";
//		// 清空response
//		response.reset();
//		// 设置response的Header
//		response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
//		OutputStream os = new BufferedOutputStream(response.getOutputStream());
//		response.setContentType("application/vnd.ms-excel;charset=gb2312");
//		// 将excel写入到输出流中
//		workbook.write(os);
//		os.flush();
//		os.close();
	}

	// 创建表头
	private static void setTitle(HSSFWorkbook workbook, HSSFSheet sheet) {
		HSSFRow row = sheet.createRow(0);
		// 设置列宽，setColumnWidth的第二个参数要乘以256，这个参数的单位是1/256个字符宽度
		sheet.setColumnWidth(8, 60 * 256);
		// 设置为居中加粗
		HSSFCellStyle style = workbook.createCellStyle();
		HSSFFont font = workbook.createFont();
		font.setBold(true);
		style.setFont(font);
		// 导出的Excel头部
		String[] headers = { "调整类型", "申请日期", "OA流程编号", "申请组织", "申请部门", "是否涉及人力成本", "调出组织", "调出部门", "调出科目", "调出月份",
				"调出金额", "查询费控系统", "调入组织", "调入部门", "调入科目", "调入月份", "调入金额", "调整原因" };
		// 设置表格默认列宽度为15个字节
		sheet.setDefaultColumnWidth((short) 16);
		for (short i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			HSSFRichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
			cell.setCellStyle(style);
		}
	}

	public static HashMap<String, HashMap<String, Resident>> readExcel(String fileName)
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		InputStream inputStream = new FileInputStream(fileName);
		System.out.println("------------------start read-------------------------------");
		Workbook workbook = WorkbookFactory.create(inputStream);
		Sheet sheet = workbook.getSheetAt(0);

		HashMap<String, HashMap<String, Resident>> allResidentsMap = new HashMap<>();

		HashMap<String, Resident> currentFamilyMap = new HashMap<>(); // 当前家庭

		DataFormatter formatter = new DataFormatter();
		int titleRowCount = 1;// 标题有一行
//		String currentFamilyTag = "";// 用来分户
		int totalNum = 0;
		for (Row row : sheet) {
			if (titleRowCount > 0) {// 去掉标题行
				titleRowCount--;
				continue;
			}

			Resident resident = new Resident();
			for (Cell cell : row) {
//				CellReference cellRef = new CellReference(row.getRowNum(), cell.getColumnIndex());
				// 单元格名称
//				String cellName = cellRef.formatAsString();
//				System.out.print(cellName);
//				System.out.print(" - ");

				// 通过获取单元格值并应用任何数据格式（Date，0.00，1.23e9，$ 1.23等），获取单元格中显示的文本
				String cellText = formatter.formatCellValue(cell);
				cellText = cell.getRichStringCellValue().getString();
//				System.out.println(cellText);
				switch (cell.getColumnIndex()) {
				case 0:
					resident.name = cellText;
					break;
				case 1:
					resident.address = getSimpleAddress(cellText);
					break;
				case 2:
					resident.contactPhone = cellText;
					break;
				case 3:
					resident.mobile = cellText;
					break;
				case 4:
					resident.residenceType = cellText;
					break;
				case 5:
					resident.idNum = cellText;
					break;
				case 6:
					resident.relationship = cellText;
					break;
				case 7:
					resident.gender = cellText;
					break;
				case 8:
					resident.residenceNum = cellText;
					break;
				default:
					System.out.println("error!!! not import this cell");
					break;
				}
			}
//			if (allResidentsMap.get(resident.address) != null) {
//				System.out.println("有这个地址了！" + resident.address);
//			}
			if (!allResidentsMap.containsKey(resident.residenceNum)) {// 不是同一户人，就用新的
				currentFamilyMap = new HashMap<>();
//				currentFamilyTag = resident.residenceNum;

				allResidentsMap.put(resident.residenceNum, currentFamilyMap);
			} else {
				currentFamilyMap = allResidentsMap.get(resident.residenceNum);
			}
//			if (resident.name.contains("陈志霞")) {// 陈志霞 杨茜 张勇泉
//				System.out.println("!!!!!!!!!!!!!!！" + resident);
//			}
			if (resident.relationship != null && resident.relationship.contains("户主")) {
				if (currentFamilyMap.containsKey("户主")) {
					System.out.println("  已经有户主了！:" + resident);
				}
				currentFamilyMap.put("户主", resident);
//				System.out.println("我是户主！");
			} else {
				if (currentFamilyMap.containsKey(resident.idNum)) {
					System.out.println("已经有这个用户了！:" + resident);
				}
				resident.relationship = "之" + resident.relationship;
				currentFamilyMap.put(resident.idNum, resident);
			}
			totalNum++;
//			System.out.println("resident:" + resident.toString());
//			System.out.println("currentFamilyMap size:" + currentFamilyMap.size());
//			System.out.println("allResidentsMap size:" + allResidentsMap.size());
//			if (1 > 0) {
//				break;
//			}
		}

		System.out.println("totalNum:" + totalNum);
		System.out.println("allResidentsMap size:" + allResidentsMap.size());
		System.out.println("------------------end read-------------------------------");
		return allResidentsMap;
	}

	private static String[] bigNumStr = { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十" };
	private static int[] bigNum = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

	/**
	 * 刘桥镇蒋一村八组2号==>8组26号
	 * 
	 * @param address
	 * @return
	 */
	public static String getSimpleAddress(String address) {
		if (address == null || address.length() == 0) {
			return "";
		}
		int cunIndex = address.indexOf("村") + 1;
		String result = address;
		if (cunIndex > 1) {
			result = address.substring(cunIndex);
		}

		int groupOffset = 0;
		if (result.contains("庙")) {
			result = result.substring(result.indexOf("庙") + 1);
			groupOffset = 21;// 十五里庙一组= 22组
		}
		String group = result.substring(0, result.indexOf("组"));
		int groupNum = 0;
		int groupLen = group.length();
		if (groupLen > 1) {// 十几二十几
			String char0 = String.valueOf(group.charAt(0));
			for (int i = 0; i < 10; i++) {
				if (char0.equals(bigNumStr[i])) {
					groupNum = i == 9 ? bigNum[i] : bigNum[i] * 10;
				}
			}
			String charEnd = String.valueOf(group.charAt(group.length() - 1));
			if (!charEnd.equals("十")) {
				for (int i = 0; i < 10; i++) {
					if (charEnd.equals(bigNumStr[i])) {
						groupNum += bigNum[i];
					}
				}
			}

			if (groupLen > 3) {
				throw new NullPointerException("没这多组啊！");
			}
		} else {
			for (int i = 0; i < 10; i++) {
				if (group.equals(bigNumStr[i])) {
					groupNum = bigNum[i];
				}
			}
		}
		groupNum += groupOffset;
		result = groupNum + result.substring(result.indexOf("组"));
//		System.out.println("adress:" + address + ">>>" + result);
		return result;
	}

//	/**
//	 * 方法名：importExcel 功能：导入 描述： 创建人：typ 创建时间：2018/10/19 11:45 修改人： 修改描述： 修改时间：
//	 */
//	public static List<Object[]> importExcel(String fileName) {
//		log.info("导入解析开始，fileName:{}", fileName);
//		try {
//			List<Object[]> list = new ArrayList<>();
//			InputStream inputStream = new FileInputStream(fileName);
//			Workbook workbook = WorkbookFactory.create(inputStream);
//			Sheet sheet = workbook.getSheetAt(0);
//			// 获取sheet的行数
//			int rows = sheet.getPhysicalNumberOfRows();
//			for (int i = 0; i < rows; i++) {
//				// 过滤表头行
//				if (i == 0) {
//					continue;
//				}
//				// 获取当前行的数据
//				Row row = sheet.getRow(i);
//				Object[] objects = new Object[row.getPhysicalNumberOfCells()];
//				int index = 0;
//				for (Cell cell : row) {
//					if (cell.getCellType().equals(NUMERIC)) {
//						objects[index] = (int) cell.getNumericCellValue();
//					}
//					if (cell.getCellType().equals(STRING)) {
//						objects[index] = cell.getStringCellValue();
//					}
//					if (cell.getCellType().equals(BOOLEAN)) {
//						objects[index] = cell.getBooleanCellValue();
//					}
//					if (cell.getCellType().equals(ERROR)) {
//						objects[index] = cell.getErrorCellValue();
//					}
//					index++;
//				}
//				list.add(objects);
//			}
//			log.info("导入文件解析成功！");
//			return list;
//		} catch (Exception e) {
//			log.info("导入文件解析失败！");
//			e.printStackTrace();
//		}
//		return null;
//	}

	// 测试导入
	public static void main(String[] args) {
//		System.out.println(getAgeFromIdNum("320624197201155325"  ));
		try {
			HashMap<String, HashMap<String, Resident>> all = readExcel("C:/Users/chengaoyang/Desktop/qiqi/户口本最新.xls");
			createExcel(all, "C:/Users/chengaoyang/Desktop/qiqi/test.xls");

		} catch (Exception e) {
			e.printStackTrace();
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
