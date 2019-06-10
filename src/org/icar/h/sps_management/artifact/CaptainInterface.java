package org.icar.h.sps_management.artifact;

import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.tools.GUIArtifact;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class CaptainInterface extends GUIArtifact {

	private DefaultListModel  model = new DefaultListModel();
	private SolutionsGUI gui = new SolutionsGUI(model);
	private List<String> plan_ref_index =new ArrayList<String>() ;


	public void setup() {
		System.out.println("sono il Captain");
		gui.setVisible(true);

		linkActionEventToOp(gui.getSelectButton(),"select");
	}

	@OPERATION
	void helloWorld() {

		System.out.println("hello world");
		signal("tick","param");
		System.out.println("mando un signal");

	}

	@INTERNAL_OPERATION
	void select(ActionEvent ev) {

		gui.setVisible(false);
		int index = gui.getSelectedForExecution();
		String selected_ref = plan_ref_index.get(index);
		signal("selected",selected_ref);


	}

	@OPERATION
	void addSolution(String plan_reference, String sol ) throws InterruptedException {
		String sol_string = "";

		/*for ( int i = 0; i< sol.size();i++)
			sol_string += sol.get(i)+"->";*/
		plan_ref_index.add(plan_reference);
		model.addElement(plan_reference+": sol" + sol);
	}


}



