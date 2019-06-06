package org.icar.h.sps_management.artifact;


import cartago.Artifact;
import cartago.OPERATION;
import org.icar.musa.scenarios.sps.SolutionsGUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CaptainInterface extends Artifact {

	private int count = 0;
	private DefaultListModel  model = new DefaultListModel();
	private SolutionsGUI gui = new SolutionsGUI(model,new GuiSelectionListener());
	private List<String> plan_ref_index =new ArrayList<String>() ;


	void init() {
		System.out.println("sono il Captain");
		gui.setVisible(true);
	}

	@OPERATION
	void helloWorld() {
		System.out.println("hello world");
	}


	@OPERATION
	void addSolution(String plan_reference, String sol ) throws InterruptedException {
		String sol_string = "";

		/*for ( int i = 0; i< sol.size();i++)
			sol_string += sol.get(i)+"->";*/
		plan_ref_index.add(plan_reference);
		count++;
		model.addElement(plan_reference+": sol" +count+ " "+ sol);
	}


	private class GuiSelectionListener implements ActionListener {


		@Override
		public void actionPerformed(ActionEvent e) {


			int index = gui.getSelectedForExecution();
			String selected_ref = plan_ref_index.get(index);
			signal("selected",selected_ref);

		}
	}


}



