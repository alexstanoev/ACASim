package simulator.stages;

public enum ExecutionUnit {

	UNKNOWN(0),
	ALU(4),
	BRANCH(1),
	LDS(1);

	private int _num;

	ExecutionUnit(int num) {
		this._num = num;
	}

	public int num() {
		return _num;
	}
}
