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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PdfExportService {

    public byte[] exportRoadmap(Roadmap roadmap, Map<Integer, Map<String, String>> weekLinksByWeek) {
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

        Set<Integer> renderedWeekLinks = new HashSet<>();
        for (Topic topic : roadmap.getTopics()) {
            table.addCell(new Phrase(String.valueOf(topic.getWeekNumber()), bodyFont));
            table.addCell(new Phrase(topic.getTopicName(), bodyFont));
            table.addCell(new Phrase(joinSubtopics(topic.getSubtopics()), bodyFont));
            table.addCell(new Phrase(topic.getMilestone(), bodyFont));
            table.addCell(new Phrase(resolveWeekLinks(topic.getWeekNumber(), weekLinksByWeek, renderedWeekLinks), bodyFont));
        }

        document.add(table);
        document.close();
        return outputStream.toByteArray();
    }

    private String joinSubtopics(List<String> subtopics) {
        if (subtopics == null || subtopics.isEmpty()) {
            return "";
        }
        return String.join(", ", subtopics);
    }

    private String resolveWeekLinks(int weekNumber,
                                    Map<Integer, Map<String, String>> weekLinksByWeek,
                                    Set<Integer> renderedWeekLinks) {
        if (weekLinksByWeek == null || weekLinksByWeek.isEmpty()) {
            return "";
        }
        Map<String, String> links = weekLinksByWeek.get(weekNumber);
        if (links == null || links.isEmpty() || renderedWeekLinks.contains(weekNumber)) {
            return "";
        }
        renderedWeekLinks.add(weekNumber);
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : links.entrySet()) {
            String label = entry.getKey();
            String url = entry.getValue();
            if (url == null || url.isBlank()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append('\n');
            }
            if (label != null && !label.isBlank()) {
                builder.append(label).append(": ");
            }
            builder.append(url);
        }
        return builder.toString();
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(230, 230, 230));
        table.addCell(cell);
    }
}
