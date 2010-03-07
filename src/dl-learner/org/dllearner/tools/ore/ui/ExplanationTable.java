package org.dllearner.tools.ore.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.dllearner.tools.ore.OREManager;
import org.dllearner.tools.ore.RepairManager;
import org.dllearner.tools.ore.RepairManagerListener;
import org.dllearner.tools.ore.explanation.Explanation;
import org.dllearner.tools.ore.ui.editor.InputVerificationStatusChangedListener;
import org.dllearner.tools.ore.ui.editor.OWLClassAxiomEditor;
import org.dllearner.tools.ore.ui.editor.VerifiedInputEditor;
import org.dllearner.tools.ore.ui.editor.VerifyingOptionPane;
import org.dllearner.tools.ore.ui.rendering.TextAreaRenderer;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.protege.editor.core.Disposable;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntologyChange;

import uk.ac.manchester.cs.owl.dlsyntax.DLSyntaxObjectRenderer;

public class ExplanationTable extends JXTable implements RepairManagerListener, Disposable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5580730282611559609L;
	
	private RepairManager repMan;
	DLSyntaxObjectRenderer renderer = new DLSyntaxObjectRenderer();
	
	protected String[] columnToolTips = {
		    null, 
		    "The number of already computed explanations where the axiom occurs.",
		    "TODO",
		    "TODO",
		    "If checked, the axiom is selected to remove from the ontology.",
		    "Edit the axiom."
		};

	
	public ExplanationTable(Explanation exp, OWLClass cl) {
		
		repMan = RepairManager.getInstance(OREManager.getInstance());
		
		repMan.addListener(this);
		setBackground(Color.WHITE);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setModel(new ExplanationTableModel(exp,	cl));
		setRolloverEnabled(true);
		addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, 
			      Color.YELLOW, Color.BLACK));  
		TableColumn column6 = getColumn(5);
		column6.setCellRenderer(new ButtonCellRenderer());
		column6.setCellEditor(new ButtonCellEditor());
		column6.setResizable(false);
//		setRowHeight(getRowHeight() + 4);
//		setRowHeightEnabled(true);
		setRowHeight(20);
	
		getColumn(0).setCellRenderer(new TextAreaRenderer());
		getColumn(1).setMaxWidth(60);
		getColumn(2).setMaxWidth(60);
		getColumn(3).setMaxWidth(60);
		getColumn(4).setMaxWidth(30);
		getColumn(5).setMaxWidth(30);
		getColumn(4).setHeaderRenderer(new TableCellRenderer() {
			
			@Override
			public Component getTableCellRendererComponent(JTable arg0, Object value,
					boolean arg2, boolean arg3, int arg4, int arg5) {
				JButton b = new JButton((Icon)value);
				return b;
			}
		});
		getColumn(4).setHeaderValue(new ImageIcon(this.getClass().getResource("../DeleteCross.gif")));
		getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					final ExplanationTable table;

					@Override
					public void valueChanged(ListSelectionEvent e) {

						table.changeSelection();

					}

					{
						table = ExplanationTable.this;

					}

				});

		addFocusListener(new FocusListener() {

			final ExplanationTable table;

			public void focusGained(FocusEvent focusevent) {
			}

			public void focusLost(FocusEvent e) {
				table.clearSelection();
				table.changeSelection();

			}

			{

				table = ExplanationTable.this;

			}
		});

		addMouseListener(new MouseAdapter() {

			final ExplanationTable table;
			{
				table = ExplanationTable.this;
			}

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					System.out.println(getValueAt(table
							.rowAtPoint(e.getPoint()), 0));
				}
			}
		});
	}
	
	@Override
	protected JTableHeader createDefaultTableHeader() {
		return new JTableHeader(columnModel) {
            /**
			 * 
			 */
			private static final long serialVersionUID = -3386641672808329591L;

			public String getToolTipText(MouseEvent e) {
             
                java.awt.Point p = e.getPoint();
                int index = columnModel.getColumnIndexAtX(p.x);
                int realIndex = 
                        columnModel.getColumn(index).getModelIndex();
                return columnToolTips[realIndex];
            }
        };

	}
	
	@Override
	public String getToolTipText(MouseEvent e){
		String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        if(rowIndex != -1){
        	
//        	tip = ((ExplanationTableModel)getModel()).getOWLAxiomAtRow(rowIndex).toString();
        	tip = renderer.render(((ExplanationTableModel)getModel()).getOWLAxiomAtRow(rowIndex));
        } else {
        	tip = super.getToolTipText(e);
        }
        return tip;
	}
	
	public void strikeOut(boolean strikeOut){
		((ExplanationTableModel)getModel()).setStriked(strikeOut);
	}
	
	private void changeSelection() {

	}
	
	class ButtonCellRenderer extends JButton implements TableCellRenderer{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1962950956976967243L;

		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
				if (isSelected) {
					setForeground(table.getSelectionForeground());
					setBackground(table.getSelectionBackground());
				} else {
					setForeground(table.getForeground());
					setBackground(UIManager.getColor("Button.background"));
				}
				setIcon(new ImageIcon(this.getClass().getResource("../Edit16.gif")));
				setText("");
				return this;
				}
				 
		
	}
	
	class ButtonCellEditor extends AbstractCellEditor implements 
			TableCellEditor, ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 9017452102605141646L;
		JTable table;
		JButton editButton;
		String text;
		

		public ButtonCellEditor() {
			super();
			
			editButton = new JButton();
			editButton.setFocusPainted(false);
			editButton.addActionListener(this);
		}

		
		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			text = (value == null) ? "" : value.toString();
			editButton.setText("");
			return editButton;
		}

		@Override
		public Object getCellEditorValue() {
			return text;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			fireEditingStopped();
			OWLClassAxiomEditor editor = new OWLClassAxiomEditor(OREManager.getInstance());
			OWLAxiom ax = ((ExplanationTableModel)getModel()).getOWLAxiomAtRow(2);
			if(ax instanceof OWLClassAxiom){
				editor.setEditedObject((OWLClassAxiom) ax);
			}
			showEditorDialog(editor, ax);
		}
	}
	
	class IconRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1729370486474583609L;

		public Component getTableCellRendererComponent(JTable table,
				Object obj, boolean isSelected, boolean hasFocus, int row,
				int column) {

			setIcon((Icon) obj);

			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			setHorizontalAlignment(JLabel.CENTER);
			return this;
		}
	}

	@Override
	public void repairPlanExecuted(List<OWLOntologyChange> changes) {
		repaint();	
	}

	@Override
	public void repairPlanChanged() {
		repaint();
	}

	@Override
	public void dispose() throws Exception {
		repMan.removeListener(this);
	}
	
	private void showEditorDialog(final OWLClassAxiomEditor editor, OWLObject value) {
		if (editor == null) {
			return;
		}
		// Create the editing component dialog - we use an option pane
		// so that the buttons and keyboard actions are what are expected
		// by the user.
		final JComponent editorComponent = editor.getEditorComponent();
		final VerifyingOptionPane optionPane = new VerifyingOptionPane(
				editorComponent) {

			public void selectInitialValue() {
				// This is overriden so that the option pane dialog default
				// button
				// doesn't get the focus.
			}
		};
		final InputVerificationStatusChangedListener verificationListener = new InputVerificationStatusChangedListener() {
			public void verifiedStatusChanged(boolean verified) {
				optionPane.setOKEnabled(verified);
			}
		};
		// if the editor is verifying, will need to prevent the OK button from
		// being available
		if (editor instanceof VerifiedInputEditor) {
			((VerifiedInputEditor) editor)
					.addStatusChangedListener(verificationListener);
		}
		final Component parent = SwingUtilities.getAncestorOfClass(Frame.class, getParent());
		final JDialog dlg = optionPane.createDialog(parent, null);
		// The editor shouldn't be modal (or should it?)
		dlg.setModal(false);
		dlg.setResizable(true);
		dlg.pack();
		dlg.setLocationRelativeTo(parent);
		dlg.addComponentListener(new ComponentAdapter() {

			public void componentHidden(ComponentEvent e) {
				Object retVal = optionPane.getValue();
				editorComponent.setPreferredSize(editorComponent.getSize());
				if (retVal != null && retVal.equals(JOptionPane.OK_OPTION)) {
					handleEditFinished(editor);
				}
//				setSelectedValue(frameObject, true);
				if (editor instanceof VerifiedInputEditor) {
					((VerifiedInputEditor) editor)
							.removeStatusChangedListener(verificationListener);
				}
//					editor.dispose();
			}
		});
			
		dlg.setTitle(OREManager.getInstance().getRendering(value));
		dlg.setVisible(true);
	}
	
        void handleEditFinished(OWLClassAxiomEditor editor){
				System.out.println(editor.getEditedObject());
        }
	

}
