import java.util.Random;
import java.util.Scanner;

public class Belady {
	public static void main(String[] args) {
		//Initialize Variables
		int noOfPageFrames, noOfPageFaults = 0, lastOpenFrame, largestNextIn, noOfDoesNotExist, firstIn;
		final int noOfAddresses = 100, pageSize = 4096;
		BeladyPage[] frames;
		byte[] addressPageNumbers = new byte[noOfAddresses];
		boolean pageFault, openFrame, nextIn;

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
		frames = new BeladyPage[noOfPageFrames];
		for(int i = 0; i < frames.length; i++){
			BeladyPage page = new BeladyPage((byte)16);
			frames[i] = page;
		}

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
				if(frames[j].pageNumber == addressPageNumbers[i]){
					pageFault = false;
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
					frames[lastOpenFrame].lastTimeIn = i;
				}
				else{
					//Loop through the frames array to see if the page number exists in the future
					for(int j = 0; j < frames.length; j++){
						nextIn = false;
						for(int k = i; k < addressPageNumbers.length; k++){
							//If the page number exists in the future, set the nextTimeIn variable to the next position of that
							//number and flip a boolean to prevent that number from being changed to another position
							if(!nextIn && frames[j].pageNumber == addressPageNumbers[k]){
								frames[j].nextTimeIn = k;
								nextIn = true;
							}
						}
						//If the number does not exist set the existsInFuture boolean to false
						if(!nextIn){
							frames[j].existsInFuture = false;
						}
					}
					//Initialize the largestNext in to zero so the loop can find which page number is the farthest away
					largestNextIn = 0;
					for(int j = 0; j < frames.length; j++){
						if(frames[j].nextTimeIn > largestNextIn){
							largestNextIn = frames[j].nextTimeIn;
						}
					}

					//Check to see how many numbers do not exist in the future to use FIFO if there more than one page does
					//not exist in the future and set that number in noOfDoesNotExist
					noOfDoesNotExist = 0;
					for(int j = 0; j < frames.length; j++){
						if(!frames[j].existsInFuture){
							noOfDoesNotExist++;
						}
					}

					//Loop through the frames array to determine which spot to replace
					for(int j = 0; j < frames.length; j++){
						//If the page is the farthest away and all numbers exist in the future replace that page
						if(frames[j].nextTimeIn == largestNextIn && noOfDoesNotExist == 0){
							frames[j].pageNumber = addressPageNumbers[i];
							frames[j].nextTimeIn = 0;
							frames[j].lastTimeIn = i;
						}
						//If only one page does not exist in the future, look for that page and replace it
						if(noOfDoesNotExist  == 1){
							for(int k = 0; k < frames.length; k++){
								if(!frames[k].existsInFuture){
									frames[k].pageNumber = addressPageNumbers[i];
									frames[k].nextTimeIn = 0;
									frames[k].lastTimeIn = i;
								}
							}
						}
						//If more than one page does not exist in the future use FIFO to determine which page to replace
						if(noOfDoesNotExist > 1){
							firstIn = noOfPageFrames + 1;
							//Loop through the frames array to check which page was in first of the pages that do not exist
							//in the future
							for(int k = 0; k < frames.length; k ++){
								if(!frames[k].existsInFuture){
									if(frames[k].lastTimeIn < firstIn){
										firstIn = frames[k].lastTimeIn;
									}
								}
							}
							//Once the first page in has been determined, loop through the array to swap out that page
							for(int k = 0; k < frames.length; k++){
								if(frames[k].lastTimeIn == firstIn){
									frames[k].pageNumber = addressPageNumbers[i];
									frames[k].nextTimeIn = 0;
									frames[k].lastTimeIn = i;
								}
							}
						}
					}
				}
				noOfPageFaults++;
			}
			//Loop through the frame array to show the process in the console
			for(int j = 0; j < frames.length; j++){
				if(frames[j].pageNumber != 16) {
					System.out.print( frames[j].pageNumber);
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

class BeladyPage{
	byte pageNumber;
	int nextTimeIn;
	int lastTimeIn;
	boolean existsInFuture;

	//Constructor to initialize a new page
	BeladyPage(byte pageNumber){
		this.pageNumber = pageNumber;
		this.nextTimeIn = 0;
		lastTimeIn = 0;
		existsInFuture = true;
	}
}