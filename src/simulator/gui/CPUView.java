package simulator.gui;

import simulator.core.ACASim;
import simulator.core.CPUMemory;
import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.IPipelineStage;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JSlider;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import net.miginfocom.swing.MigLayout;

public class CPUView extends JFrame {

	private final int MSK_OPC = 0xff000000;
	private final int MSK_OP1 = 0x00ff0000;
	private final int MSK_OP2 = 0x0000ff00;
	private final int MSK_OP3 = 0x000000ff;

	private final String MEM_BITS_FMT = "0x%08X";

	private HashMap<Integer, String> imemStages = new HashMap<Integer, String>();

	// mostly autogenerated code
	@SuppressWarnings("rawtypes")
	public CPUView() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("CPUView");
		setSize(1024, 600);

		getContentPane().setLayout(new MigLayout("", "[100.00:100.00][100.00:100.00][100.00:100.00][230.00][175.00,grow]", "[][grow][5px:5px][40.00:40.00][5px:5px]"));

		JLabel lblImem = new JLabel("Imem");
		lblImem.setFont(new Font("Noto Sans", Font.PLAIN, 16));
		getContentPane().add(lblImem, "cell 1 0,alignx center,growy");

		JLabel lblRegisters = new JLabel("Registers");
		lblRegisters.setFont(new Font("Noto Sans", Font.PLAIN, 16));
		getContentPane().add(lblRegisters, "cell 3 0,alignx center,growy");

		JLabel lblDmem = new JLabel("Dmem");
		lblDmem.setFont(new Font("Noto Sans", Font.PLAIN, 16));
		getContentPane().add(lblDmem, "cell 4 0,alignx center,growy");

		JScrollPane scrollPaneImem = new JScrollPane();
		getContentPane().add(scrollPaneImem, "cell 0 1 3 1,grow");

		listImem = new JList();
		scrollPaneImem.setViewportView(listImem);
		listImem.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollPaneRegisters = new JScrollPane();
		getContentPane().add(scrollPaneRegisters, "cell 3 1,grow");

		tblRegisters = new JTable();
		tblRegisters.setRowSelectionAllowed(false);
		tblRegisters.setFillsViewportHeight(true);
		scrollPaneRegisters.setViewportView(tblRegisters);

		JScrollPane scrollPaneDmem = new JScrollPane();
		getContentPane().add(scrollPaneDmem, "cell 4 1,grow");

		tblDmem = new JTable();
		tblDmem.setRowSelectionAllowed(false);
		tblDmem.setFillsViewportHeight(true);

		scrollPaneDmem.setViewportView(tblDmem);

		JButton btnStep = new JButton("Step");
		btnStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ACASim.getInstance().singleStep();
			}
		});
		btnStep.setFont(new Font("Noto Sans", Font.PLAIN, 16));
		getContentPane().add(btnStep, "flowx,cell 0 3,alignx center,growy");

		JButton btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(btnRun.getText().equals("Run")) {
					ACASim.getInstance().runContinuously();
					btnRun.setText("Stop");
				} else if(btnRun.getText().equals("Stop")) {
					ACASim.getInstance().pause();
					btnRun.setText("Run");
				}
			}
		});
		btnRun.setFont(new Font("Noto Sans", Font.PLAIN, 16));
		getContentPane().add(btnRun, "cell 1 3,alignx center,growy");

		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnRun.setText("Run");
				ACASim.getInstance().reset();
			}
		});
		btnReset.setFont(new Font("Noto Sans", Font.PLAIN, 16));
		getContentPane().add(btnReset, "cell 2 3,alignx center,growy");

		JSlider sliderSpeed = new JSlider();
		sliderSpeed.setSnapToTicks(true);
		sliderSpeed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ACASim.getInstance().setSleepMs(sliderSpeed.getValue());
			}
		});

		sliderSpeed.setMaximum(1000);
		sliderSpeed.setMajorTickSpacing(100);
		sliderSpeed.setPaintTicks(true);
		sliderSpeed.setValue(5);
		getContentPane().add(sliderSpeed, "cell 3 3,alignx center,aligny center");

		lblState = new JLabel("FETCH");
		lblState.setFont(new Font("Noto Sans", Font.PLAIN, 16));
		getContentPane().add(lblState, "cell 4 3");
	}

	@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
	public void setup() {
		CPUMemory mem = ACASim.getInstance().mem();

		// Imem

		listImem.setModel(new AbstractListModel() {
			public int getSize() {
				return mem.getIMemList().size();
			}
			public Object getElementAt(int index) {
				int val = mem.getIMemList().get(index);

				int opcRaw = (val & MSK_OPC) >> 24;
				int op1Raw = (val & MSK_OP1) >> 16;
				int op2Raw = (val & MSK_OP2) >> 8;
				int op3Raw = val & MSK_OP3;

				Opcode opc = Opcode.fromHex(opcRaw);

				String append = "";
				if(imemStages.containsKey(index)) {
					append = " [" + imemStages.get(index) + "]";
				}

				return opc + " " + op1Raw + " " + op2Raw + " " + op3Raw + append;
			}
		});
		listImem.setSelectedIndex(0);

		// Registers

		tblRegisters.setModel(new DefaultTableModel(
				new String[CPUMemory.NUMREGS + 2 + 7][3] ,
				new String[] {
						"Register", "Value", null
				}
				) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});


		tblRegisters.getColumnModel().getColumn(0).setResizable(false);
		tblRegisters.getColumnModel().getColumn(0).setPreferredWidth(60);
		tblRegisters.getColumnModel().getColumn(0).setMinWidth(60);
		tblRegisters.getColumnModel().getColumn(0).setMaxWidth(60);
		tblRegisters.getColumnModel().removeColumn(tblRegisters.getColumnModel().getColumn(2));

		tblRegisters.setDefaultRenderer(Object.class, new ColouredCellRenderer());


		// Dmem

		tblDmem.setModel(new DefaultTableModel(
				new String[CPUMemory.MEMSIZE][3] ,
				new String[] {
						"Address", "Value", null
				}
				) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});


		tblDmem.getColumnModel().getColumn(0).setResizable(false);
		tblDmem.getColumnModel().getColumn(0).setPreferredWidth(75);
		tblDmem.getColumnModel().getColumn(0).setMinWidth(75);
		tblDmem.getColumnModel().getColumn(0).setMaxWidth(75);
		tblDmem.getColumnModel().removeColumn(tblDmem.getColumnModel().getColumn(2));

		tblDmem.setDefaultRenderer(Object.class, new ColouredCellRenderer());

		update();
	}

	public void update() {
		CPUMemory mem = ACASim.getInstance().mem();

		listImem.setSelectedIndex(mem.PC);

		// pipeline state

		imemStages.clear();

		IPipelineStage fetchStage = ACASim.getInstance().pipeline.get(0);
		IPipelineStage decodeStage = ACASim.getInstance().pipeline.get(1);
		IPipelineStage executeStage = ACASim.getInstance().pipeline.get(2);
		IPipelineStage writeBackStage = ACASim.getInstance().pipeline.get(3);

		String fetchStr = "empty";
		String decodeStr = "empty";
		String executeStr = "empty";
		String writebackStr = "empty";
		
		// TODO FIX: check if the transaction is an instruction or a bundle and process individual instructions in it
		
		/*
		
		if(fetchStage.getCurrentTransaction() != null) {
			Instruction instr = fetchStage.getCurrentTransaction();
			imemStages.put(instr.getAddress(), "FETCH");

			int opcRaw = (instr.getRawOpcode() & MSK_OPC) >> 24;
			int op1Raw = (instr.getRawOpcode() & MSK_OP1) >> 16;
			int op2Raw = (instr.getRawOpcode() & MSK_OP2) >> 8;
			int op3Raw = instr.getRawOpcode() & MSK_OP3;

			Opcode opc = Opcode.fromHex(opcRaw);

			fetchStr = opc + " " + op1Raw + " " + op2Raw + " " + op3Raw;
		}

		
		if(decodeStage.getCurrentTransaction() != null) {
			Instruction instr = decodeStage.getCurrentTransaction();
			imemStages.put(instr.getAddress(), "DECODE");

			int op1Raw = (instr.getRawOpcode() & MSK_OP1) >> 16;
			int op2Raw = (instr.getRawOpcode() & MSK_OP2) >> 8;
			int op3Raw = instr.getRawOpcode() & MSK_OP3;

			decodeStr = instr.getOpcode() + " " + op1Raw + " " + op2Raw + " " + op3Raw;
		}

		
		if(executeStage.getCurrentTransaction() != null) {
			Instruction instr = executeStage.getCurrentTransaction();
			imemStages.put(executeStage.getCurrentTransaction().getAddress(), "EXECUTE");

			int op1Raw = (instr.getRawOpcode() & MSK_OP1) >> 16;
			int op2Raw = (instr.getRawOpcode() & MSK_OP2) >> 8;
			int op3Raw = instr.getRawOpcode() & MSK_OP3;

			executeStr = instr.getOpcode() + " " + op1Raw + " " + op2Raw + " " + op3Raw + " (" + instr.getCyclesRemaining() + ")";
		}

		
		if(writeBackStage.getCurrentTransaction() != null) {
			Instruction instr = writeBackStage.getCurrentTransaction();
			imemStages.put(writeBackStage.getCurrentTransaction().getAddress(), "WRITEBACK");

			int op1Raw = (instr.getRawOpcode() & MSK_OP1) >> 16;
			int op2Raw = (instr.getRawOpcode() & MSK_OP2) >> 8;
			int op3Raw = instr.getRawOpcode() & MSK_OP3;

			writebackStr = instr.getOpcode() + " " + op1Raw + " " + op2Raw + " " + op3Raw;
		}
		
		*/

		listImem.updateUI();

		// registers
		int i = 0;
		int k = 0;
		for(int val : mem.REG) {
			String curr = (String) tblRegisters.getModel().getValueAt(i, 1);
			String next = String.format(MEM_BITS_FMT, val) + " (" + mem.SCOREBOARD[k++] + ")";

			if(!next.equals(curr)) {
				tblRegisters.getModel().setValueAt("1", i, 2);
			} else {
				tblRegisters.getModel().setValueAt("0", i, 2);
			}

			tblRegisters.getModel().setValueAt("R" + i, i, 0);
			tblRegisters.getModel().setValueAt(next, i++, 1);
		}

		tblRegisters.getModel().setValueAt("PC", i, 0);
		tblRegisters.getModel().setValueAt(mem.PC, i++, 1);

		tblRegisters.getModel().setValueAt("CYCLES", i, 0);
		tblRegisters.getModel().setValueAt(ACASim.getInstance().clockTicks, i++, 1);

		tblRegisters.getModel().setValueAt("IRET", i, 0);
		tblRegisters.getModel().setValueAt(ACASim.getInstance().instructionsRetired, i++, 1);
		
		tblRegisters.getModel().setValueAt("IPC", i, 0);
		tblRegisters.getModel().setValueAt(ACASim.getInstance().clockTicks > 0 ? (double) ACASim.getInstance().instructionsRetired / ACASim.getInstance().clockTicks : 0, i++, 1);
		
		// stages

		tblRegisters.getModel().setValueAt("STATE", i, 0);
		tblRegisters.getModel().setValueAt(ACASim.getInstance().pipelineStage, i++, 1);

		tblRegisters.getModel().setValueAt("FETCH", i, 0);
		tblRegisters.getModel().setValueAt(fetchStr, i++, 1);

		tblRegisters.getModel().setValueAt("DECODE", i, 0);
		tblRegisters.getModel().setValueAt(decodeStr, i++, 1);

		tblRegisters.getModel().setValueAt("EXECUTE", i, 0);
		tblRegisters.getModel().setValueAt(executeStr, i++, 1);

		tblRegisters.getModel().setValueAt("WRITE", i, 0);
		tblRegisters.getModel().setValueAt(writebackStr, i++, 1);

		// memory
		int j = 0;
		for(int val : mem.DMEM) {
			String curr = (String) tblDmem.getModel().getValueAt(j, 1);
			String next = String.format(MEM_BITS_FMT, val);

			if(!next.equals(curr)) {
				tblDmem.getModel().setValueAt("1", j, 2);
			} else {
				tblDmem.getModel().setValueAt("0", j, 2);
			}
			
			tblDmem.getModel().setValueAt(String.format(MEM_BITS_FMT, j), j, 0);
			tblDmem.getModel().setValueAt(String.format(MEM_BITS_FMT, val), j++, 1);
		}

		lblState.setText(""); // TODO
	}

	public class ColouredCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, java.lang.Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			final java.awt.Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			Object val = table.getModel().getValueAt(row, 2);

			if (val != null && val.toString().equals("1")) {
				cellComponent.setForeground(Color.black);
				cellComponent.setBackground(Color.red);

			} else {
				cellComponent.setBackground(Color.white);
				cellComponent.setForeground(Color.black);
			}
			if (isSelected) {
				cellComponent.setForeground(table.getSelectionForeground());
				cellComponent.setBackground(table.getSelectionBackground());
			}

			return cellComponent;

		}

	}

	private static final long serialVersionUID = 1L;
	private JTable tblRegisters;
	private JTable tblDmem;
	@SuppressWarnings("rawtypes")
	private JList listImem;
	private JLabel lblState;

}
