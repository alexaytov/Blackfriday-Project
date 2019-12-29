package store;

public enum UserType {
    CLIENT(1),
    STAFF(2),
    HOME(3);

    private final int value;
    UserType(int value){
        this.value = value;
    }

    public static UserType getType(int value){
        if(value == UserType.CLIENT.value){
            return UserType.CLIENT;
        }else if(value == UserType.STAFF.value){
            return UserType.STAFF;
        }else{
            return UserType.HOME;
        }
    }





}
