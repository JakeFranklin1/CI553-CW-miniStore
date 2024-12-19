package catalogue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A collection of products,
 * used to record the products that are to be wished to be purchased.
 * @version 2.3
 */
@SuppressWarnings("unused")
public class Basket extends ArrayList<Product> implements Serializable
{
  private static final long serialVersionUID = 1;
  private int theOrderNum = 0; // Order number

  /**
   * Constructor for a basket which is
   * used to represent a customer order/ wish list
   */
  public Basket()
  {
    theOrderNum = 0;
  }

  /**
   * Set the customers unique order number
   * Valid order Numbers 1 .. N
   * @param anOrderNum A unique order number
   */
  public void setOrderNum(int anOrderNum)
  {
    theOrderNum = anOrderNum;
  }

  /**
   * Returns the customers unique order number
   * @return the customers order number
   */
  public int getOrderNum()
  {
    return theOrderNum;
  }

  /**
   * Add a product to the Basket.
   * Product is appended to the end of the existing products
   * in the basket.
   * @param pr A product to be added to the basket
   * @return true if successfully adds the product
   */
  @Override
  public boolean add(Product pr)
  {
    return super.add(pr); // Call add in ArrayList
  }

  /**
   * Merges products with the same product number into a single product with the combined quantity.
   * @return a list of merged products
   */
  private ArrayList<Product> mergeProducts()
  {
    Map<String, Product> productMap = new HashMap<>();
    for (Product pr : this)
    {
      if (productMap.containsKey(pr.getProductNum()))
      {
        Product existingProduct = productMap.get(pr.getProductNum());
        existingProduct.setQuantity(existingProduct.getQuantity() + pr.getQuantity());
      }
      else
      {
        productMap.put(pr.getProductNum(), new Product(pr.getProductNum(), pr.getDescription(), pr.getPrice(), pr.getQuantity()));
      }
    }
    return new ArrayList<>(productMap.values());
  }

  /**
   * Returns a description of the products in the basket suitable for printing.
   * @return a string description of the basket products
   */
  public String getDetails()
  {
    Locale uk = Locale.UK;
    StringBuilder sb = new StringBuilder(256);
    Formatter fr = new Formatter(sb, uk);
    String csign = (Currency.getInstance(uk)).getSymbol();
    double total = 0.00;
    if (theOrderNum != 0)
      fr.format("Order number: %03d\n", theOrderNum);

    ArrayList<Product> mergedProducts = mergeProducts();
    mergedProducts.sort((p1, p2) -> p1.getProductNum().compareTo(p2.getProductNum()));

    if (!mergedProducts.isEmpty())
    {
      for (Product pr : mergedProducts)
      {
        int number = pr.getQuantity();
        fr.format("%-7s", pr.getProductNum());
        fr.format("%-14.14s ", pr.getDescription());
        fr.format("(%3d) ", number);
        fr.format("%s%7.2f", csign, pr.getPrice() * number);
        fr.format("\n");
        total += pr.getPrice() * number;
      }
      fr.format("----------------------------\n");
      fr.format("Total                       ");
      fr.format("%s%7.2f\n", csign, total);
      fr.close();
    }
    return sb.toString();
  }
}