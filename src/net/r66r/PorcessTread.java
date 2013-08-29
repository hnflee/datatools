package net.r66r;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

public class PorcessTread extends Thread {
	
	
	public final static Log log = LogFactory.getLog(PorcessTread.class);
	private String fileName=null;
	private TableItem[] items=null;
	private ProgressBar progressBar=null;
	private Shell shell_process=null;
	private List listColumns=null;
	public PorcessTread(Shell shell,ProgressBar progressBar, String fileName, List listColumns) {
		// TODO Auto-generated constructor stub
		shell_process=shell;
		this.progressBar=progressBar;
		this.fileName=fileName;
		this.items=items;
		this.listColumns=listColumns;
		
	}

	private void createExcelfile(String fileName) {
		// TODO Auto-generated method stub
		
		
		
		
		if(fileName==null||fileName.equals(""))
		{
			sendMessage("请选择文件与字段类型!");
			
			return ;
		}
		String excelName=fileName.substring(0,fileName.length()-3);
		try {
			int re=writeExcel(listColumns,fileName,excelName+"xlsx");
			
			if(re>=0)
			{
				sendMessage("导出成功!");
				
				
			}
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendMessage("导出失败!");
			
		}
	}
	private  int writeExcel(List listconlume,String srcfileName,String targetFile)
			throws Exception {
		
		RandomAccessFile rddata = new RandomAccessFile(srcfileName, "r");
		LineNumberReader lnr=new   LineNumberReader(new FileReader(srcfileName));
		int countLines=0;
		lnr.skip(Long.MAX_VALUE);
		countLines=lnr.getLineNumber();
		rddata.seek(0);
		
		
		Workbook wb = new SXSSFWorkbook(1000);
		Sheet sheet = wb.createSheet();

		String dataline;
		int lines = 0;
		
		int beforValue=0;
		while ((dataline = rddata.readLine()) != null) {

			
			Row row = sheet.createRow(lines);
			String dataTmp[] = dataline.split(",");
			
			if(dataTmp.length>listconlume.size())
			{
				
				return -1;
			}
			
			for (int i = 0; i < dataTmp.length; i++) {
				
				
				
				
				Cell cell = row.createCell(i);
				if (listconlume.get(i).toString().trim()
						.equals("string")) {
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					if(dataTmp[i]==null)
					{
						
						cell.setCellValue(new String(""));
					}
					else
					{
						cell.setCellValue(new String(dataTmp[i]
								.getBytes("iso-8859-1"), "UTF-8"));
					}
					
					
				} else if (listconlume.get(i).toString().trim()
						.equals("double")) {
					if(dataTmp[i]==null||dataTmp[i].toLowerCase().equals("null"))
					{
						dataTmp[i]="0.0";
					}
					
					cell.setCellValue(Double.parseDouble(dataTmp[i]));

				} else if (listconlume.get(i ).toString().trim()
						.equals("bigint")) {
					if(dataTmp[i]==null||dataTmp[i].toLowerCase().equals("null"))
					{
						dataTmp[i]="0";
					}
					
					if(dataTmp[i].indexOf(".")!=-1)
					{
						cell.setCellValue(Long.parseLong(dataTmp[i].substring(0,dataTmp[i].indexOf("."))));
					}
					else
					{
						cell.setCellValue(Long.parseLong(dataTmp[i]));
					}
					

				}

			}
			lines++;
			
			int nowValue=(int)(lines*100/countLines);
			if(nowValue>beforValue)
			{
				log.info("完成进度："+beforValue);
				beforValue=nowValue;
				Runnable runable=new Runnable()
				{

					@Override
					public void run() {
						// TODO Auto-generated method stub
				Display.getDefault().asyncExec(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								progressBar.setSelection(progressBar.getSelection()+1);
							}
							
						});
					}
				 
				};

				new Thread(runable).start();		
				
			}
			
		}

		rddata.close();
		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream(targetFile);

		wb.write(fileOut);

		fileOut.close();
		return 0;
	}
	public void run()
	{
		createExcelfile( fileName);
	}
	
	public void sendMessage(final String message)
	{
		Runnable runable=new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
		Display.getDefault().asyncExec(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						MessageBox messageBox =	new MessageBox(shell_process, SWT.OK); 
						messageBox.setMessage(message);
						messageBox.open();
					}
					
				});
			}
		 
		};

		new Thread(runable).start();		
	}
}
