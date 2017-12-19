package simulator.stages;

public enum ExecutionUnit {

	UNKNOWN(0),
	ALU(4),
	FPU(1),
	BRANCH(1),
	LDS(3);

	private int _num;

	ExecutionUnit(int num) {
		this._num = num;
	}

	public int num() {
		return _num;
	}
}
