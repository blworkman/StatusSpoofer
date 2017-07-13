# StatusSpoofer
This program was part of a larger project for my computer music class where we were tasked with creating a 5 minute piece of computer music. 
The program here uses a list of harvested Facebook statuses to generate a new list of fake Facebook statuses that would later be used in the song. 
The statuses are generated using a first order Markov chain. For each word in the list of statuses, the program analyses what words are most likely
to come after it and generates a weighted probability based on those numbers. The program also calculates the average length and standard deviation 
the training data, and takes this into account when it generates its own statuses. As the process here is only a first-order Markov chain, the phony
statuses make very little sense over all, but each status has its own internal chain of vague comprehensibility. The final version of the music piece 
can be found on my Soundcloud profile here: https://soundcloud.com/crabflesh/final-project
