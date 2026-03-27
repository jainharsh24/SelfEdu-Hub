package com.harsh.mini_project.service;

import com.harsh.mini_project.model.Roadmap;
import com.harsh.mini_project.model.Topic;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

@Service
public class PdfExportService {

    public byte[] exportRoadmap(Roadmap roadmap) {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        document.add(new Paragraph("AI Learning Roadmap", titleFont));
        document.add(new Paragraph("Field: " + roadmap.getFieldName(), bodyFont));
        document.add(new Paragraph("Level: " + roadmap.getLevel(), bodyFont));
        document.add(new Paragraph("Duration: " + roadmap.getDurationMonths() + " months", bodyFont));
        document.add(new Paragraph("Progress: " + roadmap.getProgressPercent() + "%", bodyFont));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 3f, 4f, 3f, 3f});

        addHeaderCell(table, "Week", headerFont);
        addHeaderCell(table, "Topic", headerFont);
        addHeaderCell(table, "Subtopics", headerFont);
        addHeaderCell(table, "Milestone", headerFont);
        addHeaderCell(table, "YouTube", headerFont);

        for (Topic topic : roadmap.getTopics()) {
            table.addCell(new Phrase(String.valueOf(topic.getWeekNumber()), bodyFont));
            table.addCell(new Phrase(topic.getTopicName(), bodyFont));
            table.addCell(new Phrase(String.join(", ", topic.getSubtopics()), bodyFont));
            table.addCell(new Phrase(topic.getMilestone(), bodyFont));
            table.addCell(new Phrase(topic.getYoutubeLink(), bodyFont));
        }

        document.add(table);
        document.close();
        return outputStream.toByteArray();
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(230, 230, 230));
        table.addCell(cell);
    }
}
