package emre.recossandra.model;

public class Product {

	public static enum Color { BLACK, BLUE, RED, PINK, WHITE }

	private Long productId;
	private String productName;
	private Color color;
	private String manufacturer;
	
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	@Override
	public String toString() {
		return "Product [productId=" + productId + ", productName="
				+ productName + ", color=" + color + ", manufacturer="
				+ manufacturer + "]";
	}
	
}
