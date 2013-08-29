package net.r66r;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

public class MainFrame {

	protected Shell shlv;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Text text;
	private String fileName="";
	
	private static  final String[] COLUMN_NAME={"样例数据","数据类型"};
	private Table table;
	public final static Log log = LogFactory.getLog(MainFrame.class);
	private ProgressBar progressBar=null;
	private Display display=null;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainFrame window = new MainFrame();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		display= Display.getDefault();
		createContents();
		shlv.open();
		shlv.layout();
		while (!shlv.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlv = new Shell(SWT.CLOSE | SWT.TITLE | SWT.MIN );
		shlv.setImage(SWTResourceManager.getImage(MainFrame.class, "/com/sun/java/swing/plaf/motif/icons/DesktopIcon.gif"));
		shlv.setSize(450, 564);
		shlv.setText("易建数据转换工具V1.00");
		
		
		
		
		Label lblNewLabel = new Label(shlv, SWT.NONE);
		lblNewLabel.setBounds(10, 20, 74, 12);
		lblNewLabel.setText("1.选择文件：");
		
		Button btnNewButton = formToolkit.createButton(shlv, "\u9009\u62E9", SWT.NONE);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				text.setText("");
			FileDialog dialog=new FileDialog(shlv,SWT.OPEN);
			dialog.setFilterExtensions(new String[]{"*.csv","*.*"});
				fileName=dialog.open();
				System.out.println("----- "+fileName+" -----");
				text.setText(fileName);
				
				readFileAndSetDatatable(fileName);
			
			}

			
		});
		btnNewButton.setBounds(102, 41, 72, 22);
		
		text = new Text(shlv, SWT.BORDER);
		text.setEditable(false);
		text.setBounds(100, 17, 307, 18);
		formToolkit.adapt(text, true, true);
		
		Label lblexcel = new Label(shlv, SWT.NONE);
		lblexcel.setBounds(10, 458, 95, 12);
		lblexcel.setText("3.生成excel2007");
		
		final Button btnNewButton_1 = new Button(shlv, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List listColumns=new ArrayList(); 
				for(int i=0;i<table.getItems().length;i++)
				{
					if(table.getItems()[i].getText(1).equals("字符型"))
					{
						listColumns.add("string");
					}
					else if(table.getItems()[i].getText(1).equals("整数型数据"))
					{
						listColumns.add("bigint");
					}
					else if(table.getItems()[i].getText(1).equals("浮点型数据"))
					{
						listColumns.add("double");
					}
					
				}
				btnNewButton_1.setEnabled(false);
				progressBar.setSelection(0);
				PorcessTread pthread=new PorcessTread( shlv,progressBar, fileName, listColumns);
				pthread.start();
				btnNewButton_1.setEnabled(true);
			}

			
		});
		btnNewButton_1.setBounds(102, 479, 95, 46);
		formToolkit.adapt(btnNewButton_1, true, true);
		btnNewButton_1.setText("\u751F\u6210\u6587\u4EF6");
		
		progressBar= new ProgressBar(shlv, SWT.NONE);
		progressBar.setBounds(207, 489, 200, 22);
		
		FormToolkit formToolkit = new FormToolkit(shlv.getDisplay());
		formToolkit.adapt(progressBar, true, true);
		
		table = new Table(shlv, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(102, 84, 305, 357);
		formToolkit.adapt(table);
		formToolkit.paintBordersFor(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		
	
		
		Label lblNewLabel_1 = new Label(shlv, SWT.NONE);
		lblNewLabel_1.setBounds(10, 84, 84, 12);
		
		lblNewLabel_1.setText("2.选择数据类型");
		
		table.addListener(SWT.MeasureItem, new Listener() {
		    public void handleEvent(Event event) {
		        event.height =25;
		    }
		});
		
	
		
	
		
		
		

		
		
		for(int i=0;i<COLUMN_NAME.length;i++)
		{
			new TableColumn(table,SWT.NONE);
		}
		
		table.getColumn(0).setText("样例数据");
		table.getColumn(1).setText("数据类型");
		
	}
	
	private void readFileAndSetDatatable(String fileName) {
		// TODO Auto-generated method stub
		try {
			RandomAccessFile rddata = new RandomAccessFile(fileName, "r");
			rddata.seek(0);
			String data = rddata.readLine();//
			
			if(data!=null)
			{
				String[] values=data.split(",");
				
				log.debug("目标数据行数:"+values.length);
				
				for(int i=0;i<values.length;i++)
				{
					log.debug("生成展示数据行数:"+i);
					
					TableItem item1=new TableItem(table,SWT.NONE);
					item1.setText(new String(values[i].getBytes("iso-8859-1"),"UTF-8"));
					
					final TableEditor editor=new TableEditor(table);
					
					final Combo combo=new Combo(table,SWT.NONE);
					combo.add("字符型");
					combo.add("整数型数据");
					combo.add("浮点型数据");
					
					if(isNumeric(new String(values[i].getBytes("iso-8859-1"),"UTF-8"))){ 

						try
						{
							Integer.parseInt(new String(values[i].getBytes("iso-8859-1"),"UTF-8"));
							combo.setText("整数型数据");
						}
						catch(Exception e)
						{
							combo.setText("字符型");
						}
						
					}
					else if(isFloatPointNumber(new String(values[i].getBytes("iso-8859-1"),"UTF-8")))
					{
						try
						{
							Double.parseDouble(new String(values[i].getBytes("iso-8859-1"),"UTF-8"));
							combo.setText("浮点型数据");
						}
						catch(Exception e)
						{
							combo.setText("字符型");
						}
						
						
					}
					else 
					{
						combo.setText("字符型");
					}
					
					
					
					
					combo.setVisible(true);
					
					combo.addModifyListener(new ModifyListener()
					{
						public void modifyText(ModifyEvent e)
						{
							
							editor.getItem().setText(1,combo.getText());
						}
					});
					editor.grabHorizontal=true;
					
					editor.setEditor(combo,item1,1);
					editor.getItem().setText(1,combo.getText());
					
				}
				
				table.getColumn(0).setWidth(150);
				table.getColumn(1).setWidth(90);
				
				rddata.close();
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		
		
		
		
	}
	
	public static boolean isNumeric(String str){ 
	    Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(str).matches();    
	} 
	
	/** 
     * 判断number参数是否是浮点数表示方式 
     * @param number 
     * @return 
     */  
    public static boolean isFloatPointNumber(String number){  
        number=number.trim();  
        String pointPrefix="(\\-|\\+){0,1}\\d*\\.\\d+";//浮点数的正则表达式-小数点在中间与前面  
        String pointSuffix="(\\-|\\+){0,1}\\d+\\.";//浮点数的正则表达式-小数点在后面  
        if(number.matches(pointPrefix)||number.matches(pointSuffix))  
            return true;  
        else  
            return false;  
    } 
}
