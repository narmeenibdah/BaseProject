package application;

public class Medicine {

    private int medicineId;
    private String tradeName;
    private String unit;
    private int reorderLevel;
    private double sellingPrice;
    private boolean requiresPrescription;
    private String categoryName;

    private int totalQuantity;

    public Medicine(int medicineId, String tradeName, String unit,
                    int reorderLevel, double sellingPrice,
                    boolean requiresPrescription, String categoryName) {

        this.medicineId = medicineId;
        this.tradeName = tradeName;
        this.unit = unit;
        this.reorderLevel = reorderLevel;
        this.sellingPrice = sellingPrice;
        this.requiresPrescription = requiresPrescription;
        this.categoryName = categoryName;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public String getTradeName() {
        return tradeName;
    }

    public String getUnit() {
        return unit;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public boolean isRequiresPrescription() {
        return requiresPrescription;
    }

    public String getCategoryName() {
        return categoryName;
    }

   
    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}
