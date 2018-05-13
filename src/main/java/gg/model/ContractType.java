package gg.model;

/**
 * Created by ZN on 2015/9/2.
 */
public enum ContractType {
   CHAT(0), GETUSERINFO(32),GETALLCONTRACTS(42),CHANGESTATUS(36),OTHERSTATUSCHANGED(37),CHATPIC(3512);
    private int type;

    public static ContractType getContractTypeByCode(int code) {
        for (ContractType type : ContractType.values()) {
            if (type.getType() == code) {
                return type;
            }
        }
        return null;
    }

    private ContractType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}

