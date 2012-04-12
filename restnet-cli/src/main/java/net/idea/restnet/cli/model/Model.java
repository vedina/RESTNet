package net.idea.restnet.cli.model;

import net.idea.restnet.cli.AbstractResource;
import net.idea.restnet.cli.algorithm.Algorithm;
import net.idea.restnet.cli.dataset.Dataset;

public class Model extends AbstractResource {
	protected Algorithm algorithm;
	protected Dataset trainingDataset;
	
	public Algorithm getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}
	public Dataset getTrainingDataset() {
		return trainingDataset;
	}
	public void setTrainingDataset(Dataset trainingDataset) {
		this.trainingDataset = trainingDataset;
	}

}
