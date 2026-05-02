package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "attachments")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "visit_record_id")
    private VisitRecord visitRecord;

    private String url;
    private String type;
    private Long size;

    public Attachment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public VisitRecord getVisitRecord() { return visitRecord; }
    public void setVisitRecord(VisitRecord visitRecord) { this.visitRecord = visitRecord; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }
}
