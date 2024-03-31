import java.util.ArrayList;
import java.util.Stack;
import java.util.Iterator;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;


// Starter by Ian Gent, Oct 2022
//
// // This class is provided to save you writing some of the basic parts of the code
// // Also to provide a uniform command line structure
//
// // You may freely edit this code if you wish, e.g. adding methods to it. 
// // Obviously we are aware the starting point is provided so there is no need to explicitly credit us
// // Please clearly mark any new code that you have added/changed to make finding new bits easier for us
//
//
// // Edit history:
// // V1 released 3 Oct 2022
//
//


public class LBSMain {

      public static void printUsage() { 
          System.out.println("Input not recognised.  Usage is:");
          System.out.println("java LBSmain GEN|CHECK|SOLVE|GRACECHECK|GRACESOLVE <arguments>"  ); 
          System.out.println("     GEN arguments are seed [numpiles=17] [numranks=13] [numsuits=4] ");
          System.out.println("                       all except seed may be omitted, defaults shown");
          System.out.println("     SOLVE/GRACESOLVE argument is file]");
          System.out.println("                     if file is or is - then stdin is used");
          System.out.println("     CHECK/GRACECHECK argument is file1 [file2]");
          System.out.println("                     if file1 - then stdin is used");
          System.out.println("                     if file2 is ommitted or is - then stdin is used");
          System.out.println("                     at least one of file1/file2 must be a filename and not stdin");
	}


      public static ArrayList<Integer> readIntArray(String filename) {
        // File opening sample code from
        // https://www.w3schools.com/java/java_files_read.asp
	ArrayList<Integer> result  ;
	Scanner reader;
        try {
			File file = new File(filename);
			reader = new Scanner(file);
			result=readIntArray(reader);
			reader.close();
			return result;
            }
        catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
            }
	// drop through case
	return new ArrayList<Integer>(0);
	
        }
        

      public static ArrayList<Integer> readIntArray(Scanner reader) {
	  ArrayList<Integer> result = new ArrayList<Integer>(0);
          while( reader.hasNextInt()  ) {
              result.add(reader.nextInt());
          }
	  return result;
      }



	public static void main(String[] args) {

	Scanner stdInScanner = new Scanner(System.in);
	ArrayList<Integer> workingList;

        LBSLayout layout;

        int seed ;
        int ranks ;
        int suits ;
        int numpiles ;
       
        if(args.length < 1) { printUsage(); stdInScanner.close(); return; };


	switch (args[0].toUpperCase()) {
            //
            // Add additional commands if you wish for your own testing/evaluation
            //

		case "GEN":
			if(args.length < 2) { printUsage(); stdInScanner.close(); return; };
			seed = Integer.parseInt(args[1]);
			numpiles = (args.length < 3 ? 17 : Integer.parseInt(args[2])) ;
			ranks = (args.length < 4 ? 13 : Integer.parseInt(args[3])) ;
			suits = (args.length < 5 ? 4 : Integer.parseInt(args[4])) ;


			layout = new LBSLayout(ranks,suits);
			layout.randomise(seed,numpiles);
			layout.print();
			stdInScanner.close();
			return;
			
		case "SOLVE":
			if (args.length<2 || args[1].equals("-")) {
				layout = new LBSLayout(readIntArray(stdInScanner));
			}
			else { 
				layout = new LBSLayout(readIntArray(args[1]));
			}

			//Setup stack with initial state
			Stack<LBSState> searchStack = new Stack<LBSState>();
			LBSState initialState = new LBSState(layout);
			searchStack.push(initialState);
			Boolean solutionfound = false;
			
			//Loop until the list is empty or we have a solution
			while(searchStack.size() > 0 || solutionfound == true){
				//Pop state from stack
				LBSState currentState = searchStack.pop();
				
				//Find all possible cards to move until solution is found
				int i = 0;
				while(i < currentState.getStateLayout().layout.size()|| solutionfound == true){

					//Initialise the variables for each card being checked
					LBSState tempState = currentState;
					int currentCard = currentState.getStateLayout().cardAt(i);
					
					//Loop through the number of suits in the deck
					for (int j = 0; j < layout.numSuits(); j++){

						if(currentState.getStateLayout().layout.size()-1 >= i){
						
							int cardDestination1 = currentState.getStateLayout().cardAt(i+1);

							//See if the current card can be moved one space over
							if(((currentCard/currentState.getStateLayout().numRanks()) == (cardDestination1/currentState.getStateLayout().numRanks())) || (((((j)*currentState.getStateLayout().numRanks())+1) <= currentCard) && (currentCard <= ((j+1)*currentState.getStateLayout().numRanks()))) && (((j)*currentState.getStateLayout().numRanks())+1) <= cardDestination1 && cardDestination1 <= ((j+1)*currentState.getStateLayout().numRanks())){
							
								//Move the card
								tempState.getStateLayout().layout.set(i+1, currentCard);
						
								//Push the move i.e.the card being moved and its new pile number
								tempState.moves.push(currentCard);
								tempState.moves.push(i+1);

								//push the new state
								searchStack.push(tempState);
							}
						}

						if(currentState.getStateLayout().layout.size()-3 >= i){
							
							int cardDestination3 = currentState.getStateLayout().cardAt(i+3);
							
							//See if the current card can be moved three spaces over
							if(((currentCard/currentState.getStateLayout().numRanks()) == (cardDestination3/currentState.getStateLayout().numRanks())) || (((((j)*currentState.getStateLayout().numRanks())+1) <= currentCard) && (currentCard <= ((j+1)*currentState.getStateLayout().numRanks()))) && (((j)*currentState.getStateLayout().numRanks())+1) <= cardDestination3 && cardDestination3 <= ((j+1)*currentState.getStateLayout().numRanks())){
							
						

								//Move the card
								tempState.getStateLayout().layout.set(i+3, currentCard);

								//Push the move i.e.the card being moved and its new pile number
								tempState.moves.push(currentCard);
								tempState.moves.push(i+3);
							
								//push the new state
								searchStack.push(tempState);
							}
						}
					}
					
					//If the solution is found here then this will exit the while loop
					if(tempState.getStateLayout().layout.size() == 1) solutionfound = true;
					i++;
				}
			}
			//Since the loop finished then there's no solution so print correct message and exit
			if(searchStack.size()!=0 && solutionfound == false) System.out.println(-2);
			else if(solutionfound == false) System.out.println(-1);
			
			//Otherwise print the solution
			else{
				LBSState winningState = searchStack.pop();
				System.out.print(winningState.moves.size());

				Iterator<Integer> moveIterator = winningState.moves.iterator();
				while(moveIterator.hasNext()){
					System.out.print(" ");
					System.out.print(winningState.moves.pop());
				}
				System.out.println();
			}

			stdInScanner.close();
			return;

		case "GRACESOLVE":
		case "SOLVEGRACE":
			if (args.length<2 || args[1].equals("-")) {
				layout = new LBSLayout(readIntArray(stdInScanner));
			}
			else { 
				layout = new LBSLayout(readIntArray(args[1]));
			}
			
			//Setup stack with initial state
			Stack<LBSState> graceStack = new Stack<LBSState>();
			LBSState initialGraceState = new LBSState(layout);
			graceStack.push(initialGraceState);
			Boolean graceSolutionfound = false;

			//Loop until the list is empty or we have a solution
			while(graceStack.size() > 0 || graceSolutionfound == true){
				//Pop state from stack
				LBSState currentState = graceStack.pop();
				
				//Find all possible cards to move until solution is found
				int i = 0;
				while(i < currentState.getStateLayout().layout.size() || graceSolutionfound == true){

					//Initialise the variables for each card being checked
					LBSState tempState = currentState;
					int currentCard = currentState.getStateLayout().cardAt(i);
					int cardDestination1 = currentState.getStateLayout().cardAt(i+1);
					int cardDestination3 = currentState.getStateLayout().cardAt(i+3);
					
					//Loop through the number of suits in the deck
					for (int j = 0; j < layout.numSuits(); j++){

						//Check if the saving grace has been used 
						if(!tempState.getSavingGrace()){
							
							//Move the card to the top pile
							tempState.getStateLayout().layout.set(0, currentCard);
							
							//Push the move i.e.the card being moved and its new pile number
							tempState.moves.push(currentCard);
							tempState.moves.push(0);

							//Mark that for this run the saving grace has been used
							tempState.useSavingGrace();

							//push the new state
							graceStack.push(tempState);

						}

						//See if the current card can be moved one space over
						if(((currentCard/currentState.getStateLayout().numRanks()) == (cardDestination1/currentState.getStateLayout().numRanks())) || (((((j)*currentState.getStateLayout().numRanks())+1) <= currentCard) && (currentCard <= ((j+1)*currentState.getStateLayout().numRanks()))) && (((j)*currentState.getStateLayout().numRanks())+1) <= cardDestination1 && cardDestination1 <= ((j+1)*currentState.getStateLayout().numRanks())){
							
							//Move the card
							tempState.getStateLayout().layout.set(i+1, currentCard);
							
							//Push the move i.e.the card being moved and its new pile number
							tempState.moves.push(currentCard);
							tempState.moves.push(i+1);

							//push the new state
							graceStack.push(tempState);
						}

						//See if the current card can be moved three spaces over
						else if(((currentCard/currentState.getStateLayout().numRanks()) == (cardDestination3/currentState.getStateLayout().numRanks())) || (((((j)*currentState.getStateLayout().numRanks())+1) <= currentCard) && (currentCard <= ((j+1)*currentState.getStateLayout().numRanks()))) && (((j)*currentState.getStateLayout().numRanks())+1) <= cardDestination3 && cardDestination3 <= ((j+1)*currentState.getStateLayout().numRanks())){
							
							//Move the card
							tempState.getStateLayout().layout.set(i+3, currentCard);
							
							//Push the move i.e.the card being moved and its new pile number
							tempState.moves.push(currentCard);
							tempState.moves.push(i+3);

							//push the new state
							graceStack.push(tempState);
						}
					}

					//If the solution is found here then this will exit the while loop
					if(tempState.getStateLayout().layout.size() == 1) solutionfound = true;
					i++;
				}
			}
			//Since the loop finished then there's no solution so print correct message and exit
			if(graceStack.size()!=0 && graceSolutionfound == false) System.out.println(-2);
			else if(graceSolutionfound == false) System.out.println(-1);
			
			//Otherwise print the solution
			else{
				LBSState winningState = graceStack.pop();
				System.out.print(winningState.moves.size());

				Iterator<Integer> moveIterator = winningState.moves.iterator();
				while(moveIterator.hasNext()){
					System.out.print(" ");
					System.out.print(winningState.moves.pop());
				}
				System.out.println();
			}


			stdInScanner.close();
			return;

		case "CHECK":
			if (args.length < 2 || 
			    ( args[1].equals("-") && args.length < 3) || 
			    ( args[1].equals("-") && args[2].equals("-"))
			   ) 
			{ printUsage(); stdInScanner.close(); return; };
			if (args[1].equals("-")) {
				layout = new LBSLayout(readIntArray(stdInScanner));
			}
			else { 
				layout = new LBSLayout(readIntArray(args[1]));
			}
			if (args.length < 3 || args[2].equals("-")) {
				workingList = readIntArray(stdInScanner);
			}
			else { 
				workingList = readIntArray(args[2]);
			}

			//Loop through the set of moves
			for (int i = 1; i < workingList.size(); i=i+2) { 	
				int card = workingList.get(i);
				int cardDestination = workingList.get(i+1);
				
				
				//Check the current card is within the bounds of the deck
				if(card < 1 || cardDestination < 1 || card > layout.cardsInDeck() || cardDestination > layout.layout.size()) {
					System.out.println("false");
					return;
				}   

				int cardAtDestination = layout.layout.get(cardDestination);

				//Checks that the cards exist and are in the right position to move
				if(!((layout.layout.contains(card) && layout.layout.size() >= cardDestination) && (cardDestination == (layout.layout.indexOf(card)-1) || cardDestination == (layout.layout.indexOf(card)-3)))) {
					
					Boolean cardInSuitOrSameRank = false;
					
					//Loop through the suits to if the card can be moved to its proposed destination
					int j = 0;
					while(j < layout.numSuits() || cardInSuitOrSameRank){
						if(((card/layout.numranks) == (cardAtDestination/layout.numranks)) || (((((j)*layout.numRanks())+1) <= card) && (card <= ((j+1)*layout.numRanks()))) && (((j)*layout.numRanks())+1) <= cardAtDestination && cardAtDestination <= ((j+1)*layout.numRanks())){
							cardInSuitOrSameRank = true;
						}
						j++;
					}

					//If the card can't be moved there then output false and quit the program
					if(!cardInSuitOrSameRank){
						System.out.println("false");
						return;
					}
				}
				
				//Since the move is legal we make the move
				layout.layout.set(cardDestination, card);
		    }
			
			//After final move is made, it is correct solution if theres 1 pile remaining
			if(layout.layout.size() == 1){
				//Since the solution is correct print true and exit program
				System.out.println("true");
				return;
			}

			//Otherwise it is not and thus the solution is incorrect and exit the program
			System.out.println("false");
			stdInScanner.close();
			return;

		case "GRACECHECK":
		case "CHECKGRACE":
			if (args.length < 2 || 
			    ( args[1].equals("-") && args.length < 3) || 
			    ( args[1].equals("-") && args[2].equals("-"))
			   ) 
			   { printUsage(); stdInScanner.close(); return; };
			if (args[1].equals("-")) {
				layout = new LBSLayout(readIntArray(stdInScanner));
			}
			else { 
				layout = new LBSLayout(readIntArray(args[1]));
			}
			if (args.length < 3 || args[2].equals("-")) {
				workingList = readIntArray(stdInScanner);
			}
			else { 
				workingList = readIntArray(args[2]);
			}

                        // YOUR CODE HERE
                        
			stdInScanner.close();
			return;

		default : 
			printUsage(); 
			stdInScanner.close();
			return;

		}

	
	}
}
