package com.harsh.mini_project.service;

import java.util.List;
import java.util.Map;

public final class PredefinedRoadmapData {
    private PredefinedRoadmapData() {
    }

    // Keep keys in lowercase to match normalizeKey() usage.
    public static final Map<String, List<String>> CATALOG = Map.ofEntries(
            Map.entry("theoretical computer science", List.of(
                    "Introduction to Theoretical Computer Science",
                    "Alphabets Strings and Languages",
                    "Closure Properties",

                    "Finite Automata (FA) and Finite State Machines",
                    "Deterministic Finite Automata (DFA)",
                    "Nondeterministic Finite Automata (NFA)",
                    "NFA to DFA Conversion",
                    "Minimization of DFA",
                    "Moore and Mealy Machines",
                    "Applications of Finite Automata",

                    "Regular Expressions",
                    "Equivalence of RE and FA",
                    "Ardens Theorem",
                    "Regular Languages",
                    "Closure and Decision Properties of Regular Languages",
                    "Pumping Lemma for Regular Languages",

                    "Grammars and Chomsky Hierarchy",
                    "Regular Grammar",
                    "Context Free Grammar (CFG)",
                    "Derivations and Parse Trees",
                    "Ambiguity and Simplification of CFG",
                    "Normal Forms (CNF and GNF)",
                    "Context Free Languages and Pumping Lemma",

                    "Pushdown Automata (PDA)",
                    "Deterministic and Non Deterministic PDA",
                    "Applications of PDA",

                    "Turing Machine",
                    "Variants of Turing Machine",
                    "Universal Turing Machine",
                    "Applications and Limitations of TM",

                    "Decidability and Undecidability",
                    "Recursive and Recursively Enumerable Languages",
                    "Halting Problem",
                    "Rices Theorem",
                    "Post Correspondence Problem"
            )),
            Map.entry("software engineering", List.of(
                    "Introduction to Software Engineering",
                    "Software Process Models",
                    "Capability Maturity Model (CMM)",
                    "Waterfall Model",
                    "Incremental Model",
                    "Evolutionary Models (RAD and Spiral)",
                    "Agile Models (XP Scrum Kanban)",

                    "Requirement Engineering",
                    "Requirement Modeling",
                    "Data Flow Diagrams",
                    "Scenario Based Modeling",
                    "Software Requirement Specification (SRS)",

                    "Software Metrics",
                    "Project Estimation (LOC FP COCOMO II)",
                    "Project Scheduling and Tracking",

                    "Design Principles and Concepts",
                    "Modular Design",
                    "Cohesion and Coupling",
                    "Architectural Design",

                    "Unit Testing",
                    "Integration Testing",
                    "Validation and System Testing",
                    "White Box Testing",
                    "Black Box Testing",
                    "Software Maintenance",
                    "Re Engineering and Reverse Engineering",

                    "Risk Analysis and Management",
                    "Software Quality Assurance",
                    "Quality Metrics and Reviews",
                    "Software Reliability",
                    "Software Configuration Management",
                    "Version Control and Change Management"
            )),
            Map.entry("computer network", List.of(
                    "Introduction to Computer Networks",
                    "Network Topologies and Components",
                    "OSI Model and TCP IP Model",
                    "Connection Oriented and Connectionless Services",

                    "Physical Layer and Transmission Media",
                    "Guided Media (Twisted Pair Coaxial Fiber)",

                    "Data Link Layer Design Issues",
                    "Error Detection and Correction",
                    "Flow Control Protocols",
                    "Sliding Window Protocols",
                    "MAC Protocols (ALOHA CSMA CD)",

                    "Network Layer Concepts",
                    "IP Addressing and Subnetting",
                    "Supernetting",
                    "IPv4 and IPv6",
                    "NAT",
                    "Routing Algorithms (Dijkstra Distance Vector Link State)",
                    "ARP RARP ICMP IGMP",
                    "Congestion Control Algorithms",

                    "Transport Layer Services",
                    "UDP and TCP",
                    "TCP Flow and Congestion Control",

                    "Application Layer Protocols",
                    "DNS",
                    "HTTP",
                    "SMTP FTP Telnet DHCP"
            )),
            Map.entry("data warehousing and mining", List.of(
                    "Introduction to Data Warehouse",
                    "Data Warehouse Architecture",
                    "Data Marts",
                    "Dimensional Modeling",
                    "Star and Snowflake Schema",
                    "Fact Tables and Fact Constellation",
                    "ETL Process",
                    "OLTP vs OLAP",
                    "OLAP Operations (Slice Dice Rollup Drilldown Pivot)",

                    "Introduction to Data Mining",
                    "KDD Process",
                    "Data Mining Applications",
                    "Data Exploration and Visualization",
                    "Data Preprocessing Techniques",

                    "Classification Techniques",
                    "Decision Trees",
                    "Naive Bayes",
                    "Model Evaluation Methods",

                    "Clustering Techniques",
                    "K Means and K Medoids",
                    "Hierarchical Clustering",

                    "Association Rule Mining",
                    "Apriori Algorithm",
                    "Frequent Pattern Mining",

                    "Web Mining",
                    "Web Content Mining",
                    "Web Structure Mining",
                    "Web Usage Mining"
            )),
            Map.entry("internet programming", List.of(
                    "Introduction to Web Technology",
                    "HTTP Protocol and Web Communication",
                    "Web Clients and Servers",

                    "HTML5 Basics",
                    "HTML Forms and Semantic Elements",
                    "CSS3 Styling and Layouts",
                    "Bootstrap Basics",

                    "JavaScript Basics",
                    "DOM Manipulation",
                    "Event Handling",
                    "JSON",

                    "Servlets and JSP",
                    "Session Management and Cookies",
                    "JDBC Database Connectivity",

                    "AJAX and jQuery",
                    "Rich Internet Applications",

                    "XML and XML Parsing",
                    "PHP Basics and Web Development",

                    "React JS Basics",
                    "JSX and Simple Applications"
            )),
            Map.entry("probabilistic graphical models", List.of(
                    "Introduction to Probability Theory",
                    "Random Variables and Distributions",
                    "Conditional Independence",

                    "Graph Theory Basics",
                    "Nodes Edges Paths and Cycles",

                    "Introduction to Probabilistic Graphical Models",
                    "Bayesian Networks",
                    "Markov Models",
                    "Hidden Markov Models",

                    "Bayesian Network Modeling",
                    "Naive Bayes",
                    "Conditional Probability Distributions",
                    "D Separation",
                    "Inference in Bayesian Networks",

                    "Markov Networks",
                    "Gibbs Distribution",
                    "Parameterization of Markov Networks",
                    "Inference in Markov Models",

                    "Hidden Markov Models",
                    "Temporal Models",
                    "Inference in HMM",

                    "Learning Graphical Models",
                    "Parameter Estimation (MLE)",
                    "Overfitting and Generalization",

                    "Causality and Decision Making",
                    "Utility Theory and Decision Trees",

                    "Applications of Bayesian Networks",
                    "Applications of Markov Models",
                    "Applications of HMM"
            )),
            Map.entry("advance database management system", List.of(
                    "Introduction to Distributed Databases",
                    "Distributed DBMS Architecture",
                    "Data Fragmentation Replication and Allocation",

                    "Distributed Transaction Management",
                    "Distributed Query Processing",
                    "Distributed Concurrency Control",
                    "Recovery Techniques (2PC 3PC)",

                    "XML Databases",
                    "DTD and XML Schema",
                    "XPath and XQuery",
                    "JSON Basics and Data Types",
                    "XML vs JSON",

                    "NoSQL Concepts",
                    "SQL vs NoSQL",
                    "Replication and Sharding",
                    "CAP Theorem",
                    "ACID vs BASE",

                    "Types of NoSQL Databases",
                    "Key Value Store",
                    "Document Database",
                    "Column Family Database",

                    "MongoDB Basics",
                    "CRUD Operations in MongoDB",
                    "MongoDB Queries and Aggregation",
                    "MongoDB Replication and Sharding",

                    "Temporal Databases",
                    "Graph Databases",
                    "Spatial Databases"
            )),
            Map.entry("cryptography and system security", List.of(
                    "Introduction to Cryptography",
                    "Security Goals and Attacks",
                    "Number Theory and Modular Arithmetic",

                    "Classical Encryption Techniques",
                    "Substitution and Transposition Ciphers",

                    "Symmetric Key Cryptography",
                    "Block Cipher and Modes",
                    "DES and Triple DES",
                    "AES",
                    "Stream Cipher RC4",

                    "Public Key Cryptography",
                    "RSA Algorithm",
                    "Key Distribution and Diffie Hellman",
                    "Digital Certificates and PKI",

                    "Cryptographic Hash Functions",
                    "MD5 and SHA",
                    "MAC HMAC CMAC",

                    "Authentication Protocols",
                    "Digital Signatures",
                    "RSA Digital Signature",

                    "Network Security Basics",
                    "Network Attacks",
                    "Denial of Service Attacks",

                    "Internet Security Protocols",
                    "SSL TLS IPsec PGP",
                    "Firewalls and Intrusion Detection",

                    "System Security",
                    "Buffer Overflow",
                    "Malware (Worms Viruses)",
                    "SQL Injection"
            )),
            Map.entry("system programming and compiler construction", List.of(
                    "Introduction to System Software",
                    "Types of System Programs",
                    "Assemblers and Assembly Language",
                    "Two Pass and Single Pass Assembler",

                    "Macro Processor",
                    "Macro Definition and Expansion",
                    "Two Pass Macro Processor",

                    "Loaders and Linkers",
                    "Relocation and Linking",
                    "Dynamic Linking and Loading",

                    "Introduction to Compiler Design",
                    "Phases of Compiler",

                    "Lexical Analysis",
                    "Finite Automata in Lexical Analysis",
                    "Syntax Analysis",
                    "Top Down and Bottom Up Parsers",
                    "LL1 and SLR Parsing",

                    "Semantic Analysis",
                    "Syntax Directed Translation",

                    "Intermediate Code Generation",
                    "Three Address Code",
                    "Code Optimization",
                    "Machine Dependent and Independent Optimization",

                    "Code Generation",
                    "Basic Blocks and Flow Graphs"
            )),
            Map.entry("artificial intelligence", List.of(
                    "Introduction to Artificial Intelligence",
                    "History and Applications of AI",

                    "Intelligent Agents",
                    "Types of Agents",
                    "Problem Solving Agents",

                    "Uninformed Search Algorithms",
                    "Informed Search Algorithms",
                    "Heuristic Search (A Star)",
                    "Local Search Algorithms",
                    "Genetic Algorithms",

                    "Adversarial Search",
                    "Minimax and Alpha Beta Pruning",

                    "Knowledge Representation",
                    "Propositional Logic",
                    "First Order Logic",
                    "Inference Techniques",

                    "Uncertainty and Probabilistic Reasoning",
                    "Belief Networks",

                    "Planning Algorithms",
                    "Machine Learning Basics",
                    "Reinforcement Learning",

                    "Natural Language Processing",
                    "Robotics",
                    "AI Applications"
            )),
            Map.entry("mobile computing", List.of(
                    "Introduction to Mobile Computing",
                    "Cellular Systems and Generations",
                    "Signal Propagation and Multiplexing",
                    "Spread Spectrum Techniques",

                    "GSM Architecture and Services",
                    "GPRS and UMTS",
                    "Mobile Network Security",

                    "Mobile Networking Concepts",
                    "Mobile IP",
                    "Mobile TCP",

                    "Wireless LAN (WLAN)",
                    "IEEE 802.11 Standards",
                    "WiFi Security",
                    "Bluetooth",

                    "Mobility Management",
                    "IP Mobility and IPv6",
                    "Macro and Micro Mobility",

                    "LTE Architecture",
                    "Evolved Packet System (EPS)",
                    "Voice over LTE (VoLTE)",
                    "LTE Advanced and 5G Introduction"
            )),
            Map.entry("internet of things", List.of(
                    "Introduction to IoT",
                    "IoT Architecture and Standards",
                    "IoT Challenges",
                    "Edge Fog and Cloud Computing",

                    "Sensors and Actuators",
                    "Smart Objects",
                    "Wireless Sensor Networks",
                    "IoT Enabling Technologies (RFID NFC BLE ZigBee)",

                    "IoT Functional Stack",
                    "IoT Communication Networks",
                    "IoT Applications and Analytics",

                    "IoT Protocols",
                    "SCADA Systems",
                    "CoAP and MQTT",

                    "IoT Applications in Smart Cities",
                    "IoT in Healthcare Agriculture Energy and Industry",

                    "IoT Hardware (Arduino Raspberry Pi ESP32)",
                    "IoT Software and Platforms",
                    "REST and JSON for IoT"
            )),
            Map.entry("digital signal and image processing", List.of(
                    "Introduction to Digital Signal Processing",
                    "Discrete Time Signals",
                    "Signal Operations and Classification",
                    "Linear and Circular Convolution",
                    "Correlation and LTI Systems",

                    "Discrete Fourier Transform (DFT)",
                    "Properties of DFT",
                    "2D DFT",

                    "Fast Fourier Transform (FFT)",
                    "DIT FFT Algorithm",
                    "Spectral Analysis",

                    "Digital Image Fundamentals",
                    "Image Representation and Formats",

                    "Image Enhancement Techniques",
                    "Histogram Processing",
                    "Spatial Filtering",

                    "Image Segmentation",
                    "Edge Detection Techniques",
                    "Region Based Segmentation"
            )),
            Map.entry("quantitative analysis", List.of(
                    "Introduction to Statistics",
                    "Data Classification and Representation",

                    "Data Collection Methods",
                    "Sampling Techniques",

                    "Simple Linear Regression",
                    "Model Evaluation Metrics",

                    "Multiple Linear Regression",
                    "Hypothesis Testing in Regression",

                    "Statistical Inference",
                    "Estimation Methods",

                    "Hypothesis Testing",
                    "Type 1 and Type 2 Errors",
                    "Neyman Pearson Lemma"
            ))
            // Map.entry("dbms", List.of(
            //         "ER Model",
            //         "Normalization",
            //         "Transactions",
            //         "Indexing"
            // ))
    );
}
