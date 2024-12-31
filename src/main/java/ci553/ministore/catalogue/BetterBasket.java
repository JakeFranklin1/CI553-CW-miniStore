// package ci553.ministore.catalogue;

// // import java.io.Serializable;
// import java.util.ArrayList;
// import java.util.Currency;
// import java.util.Formatter;
// import java.util.Locale;
// import java.util.Map;
// import java.util.HashMap;

// public class BetterBasket extends Basket {
//     // private static final long serialVersionUID = 1L;

//     @Override
//     public boolean add(Product pr) {
//         boolean result = super.add(pr);
//         mergeAndSort();
//         return result;
//     }

//     @Override
//     public void removeLastItem() {
//         super.removeLastItem();
//         mergeAndSort();
//     }

//     @Override
//     public void removeByProductNum(String productNum) {
//         super.removeByProductNum(productNum);
//         mergeAndSort();
//     }

//     @Override
//     public void removeQuantityByProductNum(String productNum, int quantityToRemove) {
//         super.removeQuantityByProductNum(productNum, quantityToRemove);
//         mergeAndSort();
//     }

//     private void mergeAndSort() {
//         ArrayList<Product> mergedProducts = mergeProducts();
//         clear();
//         addAll(mergedProducts);
//         sort((p1, p2) -> p1.getProductNum().compareTo(p2.getProductNum()));
//     }

//     @Override
//     public String getDetails() {
//         Locale uk = Locale.UK;
//         StringBuilder sb = new StringBuilder(256);
//         Formatter fr = new Formatter(sb, uk);
//         String csign = (Currency.getInstance(uk)).getSymbol();
//         double total = 0.00;
//         if (getOrderNum() != 0)
//             fr.format("Order number: %03d\n", getOrderNum());

//         if (!isEmpty()) {
//             for (Product pr : this) {
//                 int number = pr.getQuantity();
//                 fr.format("%-7s", pr.getProductNum());
//                 fr.format("%-14.14s ", pr.getDescription());
//                 fr.format("(%3d) ", number);
//                 fr.format("%s%7.2f", csign, pr.getPrice() * number);
//                 fr.format("\n");
//                 total += pr.getPrice() * number;
//             }
//             fr.format("----------------------------\n");
//             fr.format("Total                       ");
//             fr.format("%s%7.2f\n", csign, total);
//             fr.close();
//         }
//         return sb.toString();
//     }

//     private ArrayList<Product> mergeProducts() {
//         Map<String, Product> productMap = new HashMap<>();
//         for (Product pr : this) {
//             if (productMap.containsKey(pr.getProductNum())) {
//                 Product existingProduct = productMap.get(pr.getProductNum());
//                 existingProduct.setQuantity(existingProduct.getQuantity() + pr.getQuantity());
//             } else {
//                 productMap.put(pr.getProductNum(),
//                         new Product(pr.getProductNum(), pr.getDescription(), pr.getPrice(), pr.getQuantity()));
//             }
//         }
//         return new ArrayList<>(productMap.values());
//     }
// }
