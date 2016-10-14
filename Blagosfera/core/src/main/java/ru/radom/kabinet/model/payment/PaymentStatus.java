package ru.radom.kabinet.model.payment;

public enum PaymentStatus {
	
	NEW {
		@Override
		public  boolean isComplete() {
			return false;
		}
	} , PROCESSING {
		@Override
		public  boolean isComplete() {
			return false;
		}
	}, SUCCESS {
		@Override
		public  boolean isComplete() {
			return true;
		}
	}, FAIL {
		@Override
		public  boolean isComplete() {
			return true;
		}
	};
	
	
	public abstract boolean isComplete();
	
}
