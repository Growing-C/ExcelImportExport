package com.cgy.cgy.utils.xp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgy.cgy.utils.ExcelUtil;

public class ExcelToJson {

	private static Logger log = LoggerFactory.getLogger(ExcelToJson.class);

	// 测试导入
	public static void main(String[] args) {
//		String[] testStrings = "fdas(fdsaf)".split("\\(");
//		log.info("testStrings:{} {}", testStrings[0], testStrings[1]);

		Map<String, CarWarningBean> d55Map = readD55Xml("C:/Users/Xpeng/Desktop/d55仪表ui/文字提示/warning.xml");

		List<CarWarningBean> carWarningList = readD55Excel(
				"C:/Users/Xpeng/Desktop/d55仪表ui/文字提示/D55_IVI_AlarmParaCfgToGpu_V07.xlsx", 1);

		List<CarWarningBean> compareList = readExcelToFulfillWarningList(
				"C:/Users/Xpeng/Desktop/d55仪表ui/存档/E28_IIC_20200604_1.xlsx", 4);
		combine(carWarningList, compareList);
		combineNew(carWarningList, d55Map);
//
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
//			    "warningId": 0,
//			    "text": "能量回收等级变为高",
//			    "text_style": "single_line",
//			    "img_res_name": "warning_default",
//			    "emergency_level": 1,
//			    "type": "GENERAL",
//		        "des":"fdaf",
//			  },
				builder.append(" {\n");
				builder.append("  \"warningId\": ").append(carWarningBean.warningId).append(",\n");
				builder.append("  \"text\": \"").append(carWarningBean.text).append("\"").append(",\n");
				if (carWarningBean.text_style == null || carWarningBean.text_style.length() == 0) {
					log.error("text_style 为空，{}！", carWarningBean.warningId);
				}
				builder.append("  \"text_style\": \"").append(carWarningBean.text_style).append("\"").append(",\n");
				if (carWarningBean.img_res_name == null || carWarningBean.img_res_name.length() == 0) {
					log.error("img_res_name 为空，{}！", carWarningBean.warningId);
				}
				builder.append("  \"img_res_name\": \"").append(carWarningBean.img_res_name).append("\",\n");
				if (carWarningBean.priority == null || carWarningBean.priority.length() == 0) {
//					builder.append("  \"priority\": ").append("\"TODO\"").append(",\n");
					builder.append("  \"emergency_level\": ").append("-1").append(",\n");
					log.error("emergency_level 为空，{}了！", carWarningBean.warningId);
				} else {
//					builder.append("  \"priority\": ").append(carWarningBean.priority).append(",\n");
					builder.append("  \"emergency_level\": ").append(carWarningBean.priority).append(",\n");
				}

				builder.append("  \"type\": \"").append(carWarningBean.type).append("\"");
				if (carWarningBean.des != null && carWarningBean.des.length() != 0) {
					builder.append(",\n").append("  \"des\": \"").append(carWarningBean.des).append("\"");
				}
				builder.append("\n");

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

	public static void combineNew(List<CarWarningBean> source, Map<String, CarWarningBean> d55Map) {
		if (source == null || source.size() == 0 || d55Map == null || d55Map.size() == 0) {
			log.info("combineNew list 为空，不合并了！");
			return;
		}
		log.info("开始合并！");
		int notFoundCount = 0;
		try {

			for (CarWarningBean carWarningBean : source) {
				CarWarningBean d55WarningBean = d55Map.get(carWarningBean.warningId);
				if (d55WarningBean == null) {
					notFoundCount++;
					log.info("carWarningBean  id:{} ,text:{}           not found!!!", carWarningBean.warningId,
							carWarningBean.text);
					carWarningBean.text_style = "";
					carWarningBean.img_res_name = "";
					continue;
				}
				carWarningBean.img_res_name = d55WarningBean.img_res_name;
				carWarningBean.text_style = d55WarningBean.text_style;
				if (!d55WarningBean.type.equals(carWarningBean.type)) {
					throw new IllegalArgumentException("type 不同！！！！！" + d55WarningBean.warningId);
				}
				if (d55WarningBean.emergency_level != null && d55WarningBean.emergency_level.length() != 0) {
					if (carWarningBean.emergency_level != null && carWarningBean.emergency_level.length() != 0) {
						if (!d55WarningBean.emergency_level.equals(carWarningBean.emergency_level)) {
							log.error("carWarningBean  id:{} ,emergency_level:{} {}  不同!!!", carWarningBean.warningId,
									carWarningBean.emergency_level, d55WarningBean.emergency_level);
							throw new IllegalArgumentException("emergency_level error!!!");
						}
					} else {
						log.info(" set carWarningBean  id:{} ,emergency_level:{} {}  !!!", carWarningBean.warningId,
								carWarningBean.emergency_level, d55WarningBean.emergency_level);
						carWarningBean.emergency_level = d55WarningBean.emergency_level;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
					CellType cellType = cell.getCellTypeEnum();
					String cellText;
					if (cellType == CellType.STRING || cellType == CellType.BLANK) {
						cellText = cell.getStringCellValue();
					} else if (cellType == CellType.NUMERIC) {
						cellText = String.valueOf((int) cell.getNumericCellValue());
						log.info(" NUMERIC -->column:{}  value:{}", column, cellText);
					} else {
						throw new IllegalArgumentException("不支持的类型：" + cellType);
					}

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
						if (cellText.contains("(")) {
							String[] allStrings = cellText.split("\\(");
							if (allStrings.length != 2) {
								throw new IllegalArgumentException("什么鬼：" + cellText);
							}
							carWarning.text = allStrings[0];
							carWarning.des = allStrings[1].replaceAll("\\)", "");
						} else if (cellText.contains("（")) {
							String[] allStrings = cellText.split("（");
							if (allStrings.length != 2) {
								throw new IllegalArgumentException("什么鬼：" + cellText);
							}
							carWarning.text = allStrings[0];
							carWarning.des = allStrings[1].replaceAll("）", "");
						} else {
							carWarning.text = cellText.replaceAll("\n", "");
						}
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

	private static Map<String, CarWarningBean> readD55Xml(String filePath) {
		File file = new File(filePath);
		if (!file.exists() || filePath == null || filePath.contains("\\.xml")) {
			log.info("readXml fileName:{} 不存在！", filePath);
			return null;
		}

		SAXReader reader = new SAXReader(); // User.hbm.xml表示你要解析的xml文档

		Document doc = null;
		Map<String, CarWarningBean> warningMap = new HashMap<>();
		try {
			// 将字符串转为XML
			doc = reader.read(file);
			// 获取根节点
			Element rootElt = doc.getRootElement();
			// 拿到根节点的名称
			System.out.println("readXml 根节点：" + rootElt.getName());

			// 获取根节点下的所有子节点
			List<Element> elements = rootElt.elements();
			for (Element element : elements) {
				Element id = element.element("id");
				Element imageRes = element.element("imgUrl");
				Element textStyle = element.element("prefabUrl");
				if (id == null || imageRes == null || textStyle == null) {
					throw new NullPointerException("有element是空的");
				}

				CarWarningBean bean = new CarWarningBean();
				bean.warningId = id.getStringValue();
				String[] imageStrings = imageRes.getStringValue().split("/");

				String imageString = imageStrings[imageStrings.length - 1];
				if (imageString.contains("Default Texture")) {
					imageString = "warning_default";
				}
				bean.img_res_name = imageString;
				String textStyleString = textStyle.getStringValue();
				if (textStyleString.contains("Single-line")) {
					bean.text_style = "single_line";
				} else if (textStyleString.contains("Multi-line")) {
					bean.text_style = "multi_line";
				} else if (textStyleString.contains("AutonomousDriving")) {
					bean.text_style = "use_template";
				} else {
					throw new IllegalArgumentException("wrong textstyle:" + textStyleString);
				}

				if (textStyleString.contains("Red")) {// 可能代表紧急程度
					bean.emergency_level = "0";
				}
				if (textStyleString.contains("ADAS") || textStyleString.contains("AutonomousDriving")) {// 自动驾驶
					bean.type = "AUTO_DRIVE";
				} else {
					bean.type = "GENERAL";
				}

				warningMap.put(id.getStringValue(), bean);
//				log.info("id: {} ,image: {},textStyle: {}", id.getStringValue(), imageString, bean.text_style);
			}

		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return warningMap;
	}
}
