package org.icar.h.sps_management.artifact;

import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.tools.GUIArtifact;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class CaptainInterface extends GUIArtifact {

	private DefaultListModel model_1 = new DefaultListModel();
	private SolutionsGUI gui_1 = new SolutionsGUI(model_1);


	private DefaultListModel model_2 = new DefaultListModel();
	private SolutionsGUI gui_2 = new SolutionsGUI(model_2);

	private List<String> plan_ref_index =new ArrayList<String>() ;


	public void setup() {
		System.out.println("sono il Captain");


		gui_1.setLocation(800,500);
		gui_1.setVisible(true);

		gui_2.setTitle("Commander GUI - Solution not validated");
		gui_2.setLocation(10,500);
		gui_2.getSelectButton().setVisible(false);
		gui_2.getJPanel3().setVisible(false);
		gui_2.setVisible(true);

		linkActionEventToOp(gui_1.getSelectButton(),"select");
	}

	@OPERATION
	void helloWorld() {

		System.out.println("hello world");
		signal("tick","param");
		System.out.println("mando un signal");

	}

	@INTERNAL_OPERATION
	void select(ActionEvent ev) {

		//gui_1.setVisible(false);
		//gui_2.setVisible(false);
		int index = gui_1.getSelectedForExecution();
		String selected_ref = plan_ref_index.get(index);
		signal("selected",selected_ref);
		plan_ref_index.clear();
		model_1.clear();
		model_2.clear();
		gui_1.getTextField().setText("none");
	}

	@OPERATION
	void addSolution(String plan_reference, String sol ) throws InterruptedException {
		plan_ref_index.add(plan_reference);
		model_1.addElement(plan_reference+": sol" + sol);
	}

	@OPERATION
	void addSolutionNotValidated(String plan_reference, String sol ) throws InterruptedException {
		plan_ref_index.add(plan_reference);
		model_2.addElement(plan_reference+": sol" + sol);
	}
}



