//Hit calculator for STNE
//By Michael Thomas
//Code provided as-is with absolutely no warranty to the extent permitted by law
//Licence: WTFPL

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;

import javax.swing.*;

public class HitCalc{
	//Variables for labels
	static String programTitle = "STNE Hit Chance Calculator v0.2 by Michael Thomas";
	static String weaponHitLabel = "Enter weapon miss chance, as written in the database";
	static String targetDodgeLabel = "Enter target evasion, as written in the database";
	static String resultsFieldLabel = "Results will be displayed here.";
	
	//Create a double to hold hit chance
    static Double hitChance = 420.69; //Should not be read at this value, present to make bugs obvious
	
	//Method for +/+
	//Ship + (1 - Ship) * Weapon
	public static Double plusPlus(double ship, double wep){
		double shipDecimal = ship/100;
		double wepDecimal = wep/100;
		double hitchance = (1-(shipDecimal + (1 - shipDecimal)*wepDecimal))*100;
		return hitchance;
	}
		
	//Method for -/+
	//Ship + Ship * Weapon
	public static Double minusPlus(double ship, double wep){
		double shipDecimal = ship/100;
		double wepDecimal = wep/100;
		double hitchance = (1-(shipDecimal + shipDecimal*wepDecimal))*100;		
		return hitchance;
	}
	
	//Method for -/-
	//Just return 0
	
	//Method for +/-
	//Weapon + Weapon * Ship
	public static Double plusMinus(double ship, double wep){
		double shipDecimal = ship/100;
		double wepDecimal = wep/100;
		double hitchance = (1-(wepDecimal + wepDecimal*shipDecimal))*100;
		return hitchance;
	}
	
	//Method to do the calculation
	public static void doCalc(JTextField weaponHit, JTextField targetDodge, JTextArea writeHere){
		//Get hit and evade chances
		String accuracyString = weaponHit.getText();
		String dodgeString = targetDodge.getText();
		boolean failed;
		double accuracy = 0;
		double evasion= 0;
		
		//Convert to doubles
		try {
			accuracy = Double.valueOf(accuracyString);
			evasion = Double.valueOf(dodgeString);
			failed = false;
		} catch (Exception e){
			writeHere.append("\nError.\nEnter the weapon miss chance and target evasion.");
			writeHere.setCaretPosition(writeHere.getDocument().getLength()); 
			failed = true;
		}
		
		if(!failed){
			if (accuracy <= 0){
				if (evasion  > 0){
					//-/+
					hitChance = minusPlus(evasion, accuracy);
				} else if (evasion <= 0){
					//-/-
					hitChance = 100.0;
				} else {
					hitChance = 420.69; //should not be reached, present to make bugs obvious
				}
			} else if (accuracy > 0){
				if (evasion  > 0){
					//+/+
					hitChance = plusPlus(evasion, accuracy);
				} else if (evasion <= 0){
					//+/-
					hitChance = plusMinus(evasion, accuracy);
				} else {
					hitChance = 420.69; //should not be reached, present to make bugs obvious
				}
			} else {
				hitChance = 420.69; //should not be reached, present to make bugs obvious
			}
			//round hitChance
			DecimalFormat twoDecimalPlaces = new DecimalFormat("#.##");
			writeHere.append("\n" + twoDecimalPlaces.format(hitChance) + "% Hit Chance");
			writeHere.setCaretPosition(writeHere.getDocument().getLength());
		} 
	}
	
	public static Object makeInterface(){
		
		//Replace crappy default theme with hopefully non-crappy system theme
		//Except on windows, because the windows theme is crap
		try {
			if (!System.getProperty("os.name").startsWith("Windows")){
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
		} catch(Exception e){}
		
		//Make the window
        JFrame window = new JFrame(programTitle);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Set up a 1x4 grid layout
        window.setLayout(new GridLayout(4,1));
        
        //Create a box to enter weapon hit
        final JTextField weaponHit = new JTextField(weaponHitLabel);
        window.add(weaponHit);

        //Create a box to enter target dodge
        final JTextField targetDodge = new JTextField(targetDodgeLabel);
        window.add(targetDodge);
        
		//Create text area to display the result
        final JTextArea resultTextArea = new JTextArea(resultsFieldLabel);
        JScrollPane resultBox = new JScrollPane(resultTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        //Create a keylistener that rejects unwanted characters for input fields
      	KeyListener floatFilter = new KeyListener(){

      		public void keyPressed(KeyEvent e) {
      			//Ignore				
      		}

			public void keyReleased(KeyEvent e) {
  				//Ignore				
   			}

   			public void keyTyped(KeyEvent e) {
   				char typed = e.getKeyChar();
   				//Execute calculation upon enter key
   				if(typed == '\n'){
   					doCalc(weaponHit, targetDodge, resultTextArea);
   				} else if(!((typed >= '0') && (typed <= '9') || typed == '.' || typed == '-' ||
   						typed == KeyEvent.VK_DELETE || typed == KeyEvent.VK_BACK_SPACE)){
   					Toolkit.getDefaultToolkit().beep();
   					e.consume();
   				} 
   			}
      		
   		};
   		
   		//Add the key listeners
   		weaponHit.addKeyListener(floatFilter);
        targetDodge.addKeyListener(floatFilter);
                
		//Set up an action for the "Calculate!" button
		class CalculateAction extends AbstractAction{
			
			JTextArea writeHere;
			public CalculateAction(String text, ImageIcon icon, Object textArea){
    			super(text, null);
    			writeHere = (JTextArea) textArea;
    		}

			public void actionPerformed(ActionEvent arg0) {
				doCalc(weaponHit, targetDodge, writeHere);
			}
			
		}
        
        //Create a button to calculate
        window.add(new JButton(new CalculateAction("Calculate!", null, resultTextArea)));
        
        //Add the text area created earlier
        window.add(resultBox);
        
        //Display the window.
        window.setPreferredSize(new Dimension(320, 400));
        window.setMinimumSize(new Dimension(250, 300));
        window.pack();
        window.setVisible(true);
        
        //Returning the text area allows us to write things there in the main thread if we want
        //For example, a welcome message
        return resultTextArea;
	}
	
	public static void main(String[] args){
		JTextArea writehere = (JTextArea) makeInterface();
	}
}
