// package ci553.ministore.catalogue;

// import java.util.Currency;
// import java.util.Formatter;
// import java.util.Locale;

// public class BetterBasket extends Basket {
//     private static final long serialVersionUID = 1L;

//     @Override
//     public boolean add(Product pr) {
//         // Check if product already exists
//         for (Product existing : this) {
//             if (existing.getProductNum().equals(pr.getProductNum())) {
//                 // Merge quantities
//                 existing.setQuantity(existing.getQuantity() + pr.getQuantity());
//                 return true;
//             }
//         }

//         // Add new product and sort
//         boolean result = super.add(pr);
//         sort((p1, p2) -> p1.getProductNum().compareTo(p2.getProductNum()));
//         return result;
//     }

//     @Override
//     public String getDetails() {
//         Locale uk = Locale.UK;
//         StringBuilder sb = new StringBuilder(256);
//         Formatter fr = new Formatter(sb, uk);
//         String csign = Currency.getInstance(uk).getSymbol();
//         double total = 0.00;

//         if (getOrderNum() != 0) {
//             fr.format("Order number: %03d\n", getOrderNum());
//         }

//         if (!isEmpty()) {
//             // Sort products by product number
//             sort((p1, p2) -> p1.getProductNum().compareTo(p2.getProductNum()));

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
// }
