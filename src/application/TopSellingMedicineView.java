package application;

public class TopSellingMedicineView {

    private int medicineId;
    private String tradeName;
    private int totalSold;

    public TopSellingMedicineView(int medicineId, String tradeName, int totalSold) {
        this.medicineId = medicineId;
        this.tradeName = tradeName;
        this.totalSold = totalSold;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public String getTradeName() {
        return tradeName;
    }

    public int getTotalSold() {
        return totalSold;
    }
}
