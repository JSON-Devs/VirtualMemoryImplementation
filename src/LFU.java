import java.util.Random;
import java.util.Scanner;

public class LFU {
	public static void main(String[] args) {
		//Initialize Variables
		int noOfPageFrames, noOfPageFaults = 0, lastOpenFrame;
		final int noOfAddresses = 100, pageSize = 4096;
		Page[] frames;
		byte[] addressPageNumbers = new byte[noOfAddresses];
		boolean pageFault, openFrame;
		int smallestPageCount, earliestPageIn;

		//Get the number of page frames available from the user
		Scanner keyboard = new Scanner(System.in);
		System.out.println("How many page frames are available for the process? (2-7)");
		noOfPageFrames = keyboard.nextInt();
		//Validate so that the input is between 2 and 7
		while(noOfPageFrames < 2 || noOfPageFrames > 7){
			System.out.println("Error: Please enter a number between 2 and 7");
			noOfPageFrames = keyboard.nextInt();
		}

		//Initialize the frame array and set all values to a value outside of the max page number
		//Max page number is 15 so value set is 16
		frames = new Page[noOfPageFrames];
		for(int i = 0; i < frames.length; i++){
			Page page = new Page((byte)16);
			frames[i] = page;
		}

		//Create random 16-bit addresses and find their page number basked on 4k page sizes and print out the result
		for(int i = 0; i < noOfAddresses; i++){
			Random randomNum = new Random();
			addressPageNumbers[i] = (byte) (randomNum.nextInt(Short.MAX_VALUE + 1) / pageSize);
			System.out.print(addressPageNumbers[i] + " - ");
		}
		System.out.print("\n");

		//Loop through the address page numbers
		for(int i = 0; i < addressPageNumbers.length; i++){
			pageFault = true;
			//Check to see if the page number already exists in the frames
			for(int j = 0; j < frames.length; j++){
				//If it exists, set pageFault to false
				if(frames[j].pageNumber == addressPageNumbers[i]){
					pageFault = false;
					frames[j].count++;
				}
			}
			//If there was a page fault add the new page into the frame
			if(pageFault){
				openFrame = false;
				lastOpenFrame = 0;
				//Loop through the frame array to check and see if there is an open space in the array
				for(int j = 0; j < frames.length; j++){
					if(frames[j].pageNumber == 16 && !openFrame){
						openFrame = true;
						lastOpenFrame = j;
					}
				}
				//If there is an open space in the array, add the page into that open frame
				if(openFrame){
					frames[lastOpenFrame].pageNumber = addressPageNumbers[i];
					frames[lastOpenFrame].count++;
					frames[lastOpenFrame].whenWasLastTimeIn = i;
				}
				else{
					smallestPageCount = noOfAddresses + 1;
					earliestPageIn = noOfAddresses + 1;
					//Loop through the array to set the smallest page count and earliest page in for replacement
					for(int j = 0; j < frames.length; j++){
						if(frames[j].count < smallestPageCount){
							smallestPageCount = frames[j].count;
						}

						if(frames[j].count == smallestPageCount && frames[j].whenWasLastTimeIn < earliestPageIn){
							earliestPageIn = frames[j].whenWasLastTimeIn;
						}
					}
					//Loop through the frame array to set the page into the correct frame
					for(int j = 0; j < frames.length; j++){
						if(frames[j].whenWasLastTimeIn == earliestPageIn){
							frames[j].pageNumber = addressPageNumbers[i];
							frames[j].count = 1;
							frames[j].whenWasLastTimeIn = i;
						}
					}
				}
				noOfPageFaults++;
			}
			System.out.print((i + 1) +" => ");
			//Loop through the frame array to show the process in the console
			for(int j = 0; j < frames.length; j++){
				if(frames[j].pageNumber != 16) {
					System.out.print(j + ": " + frames[j].pageNumber);
				}else{
					System.out.print(j + ":  ");
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

class Page{
	byte pageNumber;
	int count;
	int whenWasLastTimeIn;

	//Constructor to initialize a new page
	Page(byte pageNumber){
		this.pageNumber = pageNumber;
		this.count = 0;
		this.whenWasLastTimeIn = 0;
	}
}