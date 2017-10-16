package simulator.gui;

import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import simulator.core.ACASim;
import simulator.core.CPUMemory;
import simulator.instructions.Opcode;

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
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class CPUView extends JFrame {

	private final int MSK_OPC = 0xff000000;
	private final int MSK_OP1 = 0x00ff0000;
	private final int MSK_OP2 = 0x0000ff00;
	private final int MSK_OP3 = 0x000000ff;

	private JList listImem;
	public CPUView() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("CPUView");
		setSize(1024, 600);

		getContentPane().setLayout(new MigLayout("", "[100.00:100.00][100.00:100.00][100.00:100.00][230.00][174.00,grow]", "[][grow][5px:5px][40.00:40.00][5px:5px]"));

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

		/*
		listImem.setModel(new AbstractListModel() {
			String[] values = new String[] {"ADDI R1 R2 R3", "ADDI R1 R2 R3", "MUL R1 R2 R3", "ADD R1 R2 R3", "HALT"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		listImem.setSelectedIndex(2);
		 */

		JScrollPane scrollPaneRegisters = new JScrollPane();
		getContentPane().add(scrollPaneRegisters, "cell 3 1,grow");

		tblRegisters = new JTable();
		tblRegisters.setCellSelectionEnabled(true);
		tblRegisters.setRowSelectionAllowed(false);
		tblRegisters.setFillsViewportHeight(true);
		scrollPaneRegisters.setViewportView(tblRegisters);

		JScrollPane scrollPaneDmem = new JScrollPane();
		getContentPane().add(scrollPaneDmem, "cell 4 1,grow");

		tblDmem = new JTable();
		tblDmem.setCellSelectionEnabled(true);
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
		sliderSpeed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ACASim.getInstance().setSleepMs(sliderSpeed.getValue());
			}
		});

		sliderSpeed.setMaximum(1000);
		sliderSpeed.setMajorTickSpacing(200);
		sliderSpeed.setPaintTicks(true);
		sliderSpeed.setPaintLabels(true);
		sliderSpeed.setValue(5);
		getContentPane().add(sliderSpeed, "cell 3 3,alignx center,aligny center");

		JLabel lblState = new JLabel("FETCH");
		lblState.setFont(new Font("Noto Sans", Font.PLAIN, 16));
		getContentPane().add(lblState, "cell 4 3");
	}

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

				return opc + " " + op1Raw + " " + op2Raw + " " + op3Raw;
			}
		});
		listImem.setSelectedIndex(0);

		// Registers

		tblRegisters.setModel(new DefaultTableModel(
				new String[CPUMemory.NUMREGS + 2][3] ,
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

		// registers
		int i = 0;
		for(int val : mem.REG) {
			String curr = (String) tblRegisters.getModel().getValueAt(i, 1);
			String next = String.format("0x%08X", val);

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

		// memory
		int j = 0;
		for(int val : mem.DMEM) {
			tblDmem.getModel().setValueAt(String.format("0x%08X", j), j, 0);
			tblDmem.getModel().setValueAt(String.format("0x%08X", val), j++, 1);
		}
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


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable tblRegisters;
	private JTable tblDmem;

}
