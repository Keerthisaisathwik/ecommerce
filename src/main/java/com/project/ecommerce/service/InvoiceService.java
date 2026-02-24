package com.project.ecommerce.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.OrderItem;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;


@Service
public class InvoiceService {

    public byte[] generateInvoice(Order order) throws Exception {

        // Fonts
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font formTextFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

// Date formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm:ss a");


// PDF Setup
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);

        document.open();

// ===== TITLE =====
        Paragraph title = new Paragraph("INVOICE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(15f);
        document.add(title);

// ===== ORDER DETAILS =====
        Paragraph orderId = new Paragraph();
        orderId.add(new Phrase("Order ID: ", boldFont));
        orderId.add(new Phrase(order.getOrderNumber(), normalFont));
        document.add(orderId);

        Paragraph orderDate = new Paragraph();
        orderDate.add(new Phrase("Date: ", boldFont));
        orderDate.add(new Phrase(order.getCreatedAt().format(formatter), normalFont));
        document.add(orderDate);

        Paragraph billing = new Paragraph();
        billing.add(new Phrase("Billing Address: ", boldFont));
        billing.add(new Phrase(order.getBillingAddress(), normalFont));
        document.add(billing);

        Paragraph shipping = new Paragraph();
        shipping.add(new Phrase("Delivery Address: ", boldFont));
        shipping.add(new Phrase(order.getDeliveryAddress(), normalFont));
        document.add(shipping);

        if (order.getDeliveredAt() != null) {
            Paragraph delivered = new Paragraph();
            delivered.add(new Phrase("Delivered At: ", boldFont));
            delivered.add(new Phrase(order.getDeliveredAt().format(formatter), normalFont));
            document.add(delivered);
        }

// Space after section
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100);

        /* ================= HEADER ================= */

// Header titles
        String[] headers = {
                "SI.No",
                "Product",
                "Unit Price",
                "Quantity",
                "Net Amount",
                "Tax Rate",
                "Tax Type",
                "Tax Per Piece",
                "Total Tax Amount",
                "Total Amount"
        };

// Added headers to table
        for (String header : headers) {

            PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));

            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerCell.setPadding(6);

            // Optional professional styling
            headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            headerCell.setBorderWidthBottom(1.5f);

            table.addCell(headerCell);
        }

        /* ================= ITEMS ================= */

        int count = 1;

        for (OrderItem item : order.getItems()) {
            table.addCell(new Phrase(String.valueOf(count++), formTextFont));
            table.addCell(new Phrase(item.getProductName(), formTextFont));
            table.addCell(new Phrase(String.format("%.2f", item.getUnitPriceWithoutTax()),
                    formTextFont));
            table.addCell(new Phrase(String.valueOf(item.getQuantity()), formTextFont));
            table.addCell(new Phrase(String.format("%.2f", item.getNetAmount()), formTextFont));
            table.addCell(new Phrase(String.format("%.2f", item.getTaxRate()), formTextFont));
            table.addCell(new Phrase("GST", formTextFont));
            table.addCell(new Phrase(String.format("%.2f", item.getTaxPerUnit()), formTextFont));
            table.addCell(new Phrase(String.format("%.2f", item.getTotalTaxAmount()),
                    formTextFont));
            table.addCell(new Phrase(String.format("%.2f", item.getTotalAmount()), formTextFont));
        }

        /* ================= TOTAL ROW (ONE ROW ONLY) ================= */

        /* ========= COLUMN 1 ========= */
        PdfPCell col1 = new PdfPCell(new Phrase("Total", boldFont));
        col1.setHorizontalAlignment(Element.ALIGN_LEFT);
        col1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
        table.addCell(col1);

        /* ========= COLUMNS 2–7 ========= */
        for (int i = 0; i < 6; i++) {
            PdfPCell middle = new PdfPCell(new Phrase(""));
            middle.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
            table.addCell(middle);
        }

        /* ========= COLUMN 8 ========= */
        PdfPCell col8 = new PdfPCell(new Phrase(""));
        col8.setBorder(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
        table.addCell(col8);

        /* ========= COLUMN 9 (Total Tax) ========= */
        PdfPCell taxCell = new PdfPCell(new Phrase(String.format("%.2f", order.getTax()),
                boldFont));
        taxCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        taxCell.setBorder(Rectangle.BOX);  // full border
        table.addCell(taxCell);

        /* ========= COLUMN 10 (Grand Total) ========= */
        PdfPCell grandCell = new PdfPCell(new Phrase(String.format("%.2f",
                order.getTotalAmount()), boldFont));
        grandCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        grandCell.setBorder(Rectangle.BOX);  // full border
        table.addCell(grandCell);


        document.add(table);


        document.add(new Paragraph(" "));

// Create table
        PdfPTable paymentTable = new PdfPTable(2);
        paymentTable.setWidthPercentage(100);
        paymentTable.setSpacingBefore(10f);
        paymentTable.setSpacingAfter(10f);

// Remove default borders for cleaner look
        paymentTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

// ---- Row 1 ----
        PdfPCell cell1 = new PdfPCell();
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.addElement(new Phrase("Payment Transaction Id: ", boldFont));
        cell1.addElement(new Phrase(order.getPaymentTransactionId(), normalFont));
        paymentTable.addCell(cell1);

        PdfPCell cell2 = new PdfPCell();
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.addElement(new Phrase("Date & Time: ", boldFont));
        cell2.addElement(new Phrase(order.getCreatedAt().format(formatter), normalFont));
        paymentTable.addCell(cell2);

// ---- Row 2 ----
        PdfPCell cell3 = new PdfPCell();
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.addElement(new Phrase("Invoice Value: ", boldFont));
        cell3.addElement(new Phrase("₹" + String.format("%.2f", order.getTotalAmount()),
                normalFont));
        paymentTable.addCell(cell3);

        PdfPCell cell4 = new PdfPCell();
        cell4.setBorder(Rectangle.NO_BORDER);
        cell4.addElement(new Phrase("Mode of Payment: ", boldFont));
        cell4.addElement(new Phrase(String.valueOf(order.getPaymentMethod()), normalFont));
        paymentTable.addCell(cell4);

// Add table to document
        document.add(paymentTable);

// Space
        document.add(new Paragraph(" "));

// Grand Total (Fully Bold)
        Font grandFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Paragraph grandTotalAmount = new Paragraph(
                "Grand Total: ₹" + String.format("%.2f", order.getTotalAmount()),
                grandFont);
        grandTotalAmount.setAlignment(Element.ALIGN_RIGHT);

        document.add(grandTotalAmount);


        document.close();

        return out.toByteArray();
    }
}
