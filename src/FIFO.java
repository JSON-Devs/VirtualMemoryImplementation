import java.util.Random;
import java.util.Scanner;

public class FIFO {

    public static void main(String[] args) {
    	//Initialize Variables
    	int lastPageIn, noOfPageFrames, noOfPageFaults = 0;
    	final int noOfAddresses = 100, pageSize = 4096;
    	byte[] frames;
		byte[] addressPageNumbers = new byte[noOfAddresses];
		boolean pageFault;

		//Get the number of page frames available from the user
		Scanner keyboard = new Scanner(System.in);
		System.out.println("How many page frames are available for the process? (2-7)");
		noOfPageFrames = keyboard.nextInt();
		//Validate so that the input is between 2 and 7
		while(noOfPageFrames < 2 || noOfPageFrames > 7){
			System.out.println("Error: Please enter a number between 2 and 7");
			noOfPageFrames = keyboard.nextInt();
		}
		keyboard.close();

		//Initialize the frame array and set all values to a value outside of the max page number
		//Max page number is 15 so value set is 16
		frames = new byte[noOfPageFrames];
		for(int i = 0; i < frames.length; i++){
			frames[i] = 16;
		}

		//Initialize the last page in as the last spot in the array
		lastPageIn = frames.length - 1;

		//Create random 16-bit addresses and find their page number basked on 4k page sizes and print out the result
		for(int i = 0; i < noOfAddresses; i++){
			Random randomNum = new Random();
			addressPageNumbers[i] = (byte) (randomNum.nextInt(Short.MAX_VALUE + 1) / pageSize);
			System.out.print(addressPageNumbers[i] + " - ");
		}
		System.out.print("\n\n");

		//Loop through the address page numbers
		for(int i = 0; i < addressPageNumbers.length; i++){
			pageFault = true;
			//Check to see if the page number already exists in the frames
			for(int j = 0; j < frames.length; j++){
				//If it exists, set pageFault to false
				if(frames[j] == addressPageNumbers[i]){
					pageFault = false;
				}
			}
			//If there was a page fault add the new page into the frame
			if(pageFault){
				//If the last page in was the last spot in the array the next page out is place 0 and replace that value
				if(lastPageIn == (frames.length - 1)){
					frames[0] = addressPageNumbers[i];
					lastPageIn = 0;
				}
				else{
					frames[lastPageIn + 1] = addressPageNumbers[i];
					lastPageIn++;
				}
				noOfPageFaults++;
			}
			//Loop through the frame array to show the process in the console
			for(int j = 0; j < frames.length; j++){
				if(frames[j] != 16) {
					System.out.print(frames[j]);
				}
				if( j != frames.length - 1){
					System.out.print(" - ");
				}
			}
			System.out.print('\n');
		}
		System.out.println("Number of Page Faults: " + noOfPageFaults);
	}
}
