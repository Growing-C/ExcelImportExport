package com.cgy.cgy.utils.xp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgy.cgy.utils.ExcelUtil;

public class ExcelToJson {

	private static Logger log = LoggerFactory.getLogger(ExcelToJson.class);

	// 测试导入
	public static void main(String[] args) {
		List<CarWarningBean> carWarningList = readD55Excel(
				"C:/Users/Xpeng/Desktop/excel/D55_IVI_AlarmParaCfg - ToGpu.xlsx", 1);

		List<CarWarningBean> compareList = readExcelToFulfillWarningList(
				"C:/Users/Xpeng/Desktop/excel/E28_IIC_20200604_1.xlsx", 4);
		combine(carWarningList, compareList);

		generateJsonFile(carWarningList, "C:/Users/Xpeng/Desktop/excel/warning_list.json");
	}

	/**
	 * 生成json文件
	 * 
	 * @param list
	 * @param fileName
	 */
	public static void generateJsonFile(List<CarWarningBean> list, String fileName) {
		if (list == null || list.size() == 0) {
			log.info("CarWarningBean list is null!!");
			return;
		}
		log.info("开始生成ios文件！fileName:{}", fileName);
		File file = new File(fileName);
		try {
			ExcelUtil.createFile(file);

//			FileOutputStream out = new FileOutputStream(file, true);

			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
			BufferedWriter writer = new BufferedWriter(write);

			int size = list.size();

			writer.write("[\n");
			for (int i = 0; i < size; i++) {
				StringBuilder builder = new StringBuilder();
				CarWarningBean carWarningBean = list.get(i);
//				  {
//			    "warningId": 4,
//			    "text": "请切换至非P挡释放驻车",
//			    "img_res_name": "indicator_lock",
//			    "priority": 0,
//			    "emergency_level": 0,
//			    "type": "GENERAL"
//			  },
				builder.append(" {\n");
				builder.append("  \"warningId\": ").append(carWarningBean.warningId).append(",\n");
				builder.append("  \"text\": \"").append(carWarningBean.text).append("\"").append(",\n");
				builder.append("  \"img_res_name\": \"\"").append(",\n");
				if (carWarningBean.priority == null || carWarningBean.priority.length() == 0) {
//					builder.append("  \"priority\": ").append("\"TODO\"").append(",\n");
					builder.append("  \"emergency_level\": ").append("-1").append(",\n");
				} else {
//					builder.append("  \"priority\": ").append(carWarningBean.priority).append(",\n");
					builder.append("  \"emergency_level\": ").append(carWarningBean.priority).append(",\n");
				}

				builder.append("  \"type\": \"").append(carWarningBean.type).append("\"").append("\n");

				builder.append("},\n");
				writer.write(builder.toString());
			}

			writer.write("]\n");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("生成.json文件成功！");
	}

	/**
	 * 移除特殊字符，再来比较
	 * 
	 * @param source
	 * @param compare
	 * @return
	 */
	public static boolean compareIgnoreSpecificCharacter(String source, String compare) {
		return compare.replaceAll("\n", "").replaceAll("，", "").replaceAll("：", "")
				.contains(source.replaceAll("\n", "").replaceAll("，", "").replaceAll("：", ""));
	}

	public static void combine(List<CarWarningBean> source, List<CarWarningBean> compareList) {
		if (source == null || source.size() == 0 || compareList == null || compareList.size() == 0) {
			log.info("list 为空，不合并了！");
			return;
		}
		log.info("开始合并！");
		int notFoundCount = 0;
		A: for (CarWarningBean carWarningBean : source) {
			for (CarWarningBean compareBean : compareList) {
				if (compareIgnoreSpecificCharacter(carWarningBean.text, compareBean.text)) {
					carWarningBean.priority = compareBean.priority;
					carWarningBean.emergency_level = compareBean.priority;
					continue A;
				}
			}
			notFoundCount++;
			log.info("carWarningBean  id:{} ,text:{}           not found!!!", carWarningBean.warningId,
					carWarningBean.text);
		}

		log.info("合并完成！共有{}个未找到", notFoundCount);
	}

	/**
	 * 读取d55 warning
	 * 
	 * @param folderName
	 * @param fileN
	 * @param startLine  开始行 0表示从第一行开始，1表示 从第二行开始（跳过第一行）
	 * @return
	 */
	public static List<CarWarningBean> readD55Excel(String fileName, int startLine) {
		if (fileName == null || !fileName.contains(".xls")) {
			log.info("fileName:{} 不是excel，不解析", fileName);
			return null;
		}
		log.info("导入excel解析开始，fileName:{}", fileName);
		try {
			List<CarWarningBean> list = new ArrayList<>();
			InputStream inputStream = new FileInputStream(fileName);
			Workbook workbook = WorkbookFactory.create(inputStream);
			Sheet sheet = workbook.getSheetAt(1);
			// 获取sheet的行数
			int rows = sheet.getPhysicalNumberOfRows();
			for (int i = 0; i < rows; i++) {
				// 过滤表头行
				if (i < startLine) {
					continue;
				}
				// 获取当前行的数据
				Row row = sheet.getRow(i);
				if (row.getPhysicalNumberOfCells() < 3) {
					continue;
				}
				CarWarningBean carWarning = new CarWarningBean();

				for (Cell cell : row) {
					int column = cell.getColumnIndex();
					String cellText = cell.getStringCellValue();

					if (column == 0) {
						if (cellText == null || cellText.length() == 0) {
							throw new NullPointerException("warningId is null!");
						}
						carWarning.warningId = cellText;
					} else if (column == 1) {
						if (cellText == null || cellText.length() == 0) {
							throw new NullPointerException("Owner is null!!");
						}
						if (cellText.equals("GENERAL_AREA")) {
							carWarning.type = "GENERAL";
						} else if (cellText.equals("AUTODRIVE_AREA")) {
							carWarning.type = "AUTO_DRIVE";
						} else {
							throw new NullPointerException("Owner is unsupported:" + cellText);
						}

					} else if (column == 2) {
						if (cellText == null || cellText.length() == 0) {
							throw new NullPointerException("text is null!!");
						}
						carWarning.text = cellText.replaceAll("\n", "");
					}
				}

				list.add(carWarning);
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
	 * 从第二个excel 找到要对比的信息
	 * 
	 * @param fileName
	 * @param startLine
	 * @param warningList
	 * @return
	 */
	public static List<CarWarningBean> readExcelToFulfillWarningList(String fileName, int startLine) {
		if (fileName == null || !fileName.contains(".xls")) {
			log.info("fileName:{} 不是excel，不解析", fileName);
			return null;
		}

		log.info("导入excel解析开始，fileName:{}", fileName);
		try {
			List<CarWarningBean> e28List = new ArrayList<>();
			InputStream inputStream = new FileInputStream(fileName);
			Workbook workbook = WorkbookFactory.create(inputStream);
			Sheet sheet = workbook.getSheetAt(25);
			// 获取sheet的行数
			int rows = sheet.getPhysicalNumberOfRows();
			for (int i = 0; i < rows; i++) {
				// 过滤表头行
				if (i < startLine) {
					continue;
				}
				// 获取当前行的数据
				Row row = sheet.getRow(i);
				if (row.getPhysicalNumberOfCells() < 5) {
					continue;
				}

				String infoText = row.getCell(1).getStringCellValue();
				String priority = row.getCell(8).getStringCellValue();
				if (infoText == null || infoText.length() == 0 || priority == null || priority.length() == 0) {
					log.info("linenum:{} infoText:{}, priority:{} is null", i, infoText, priority);
					continue;
				}

				CarWarningBean carWarning = new CarWarningBean();
//				log.info("line num :{} infoText:{} ,priority:{} ", i, infoText, priority);
				carWarning.text = infoText;
				if (priority.equals("高")) {
					carWarning.priority = "0";
				} else if (priority.equals("中")) {
					carWarning.priority = "1";
				} else if (priority.equals("低")) {
					carWarning.priority = "1";
				} else {
					throw new NullPointerException("priority is unsupported:" + priority);
				}

				e28List.add(carWarning);
			}
			log.info("导入文件解析成功！总数：" + e28List.size());

			return e28List;
		} catch (Exception e) {
			log.info("导入文件解析失败！");
			e.printStackTrace();
		}
		return null;
	}
}
