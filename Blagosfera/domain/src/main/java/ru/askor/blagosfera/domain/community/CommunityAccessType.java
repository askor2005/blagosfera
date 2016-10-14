package ru.askor.blagosfera.domain.community;

public enum CommunityAccessType {

	OPEN {
		@Override
		public String toString() {
			return "Открытое";
		}

	},
	CLOSE {
		@Override
		public String toString() {
			return "Закрытое";
		}
	},
	RESTRICTED {
		@Override
		public String toString() {
			return "Ограниченное";
		}
	}

}
