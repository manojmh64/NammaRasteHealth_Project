package com.nammaraste.health.data.seed

import com.nammaraste.health.data.db.AppDatabase
import com.nammaraste.health.data.db.entity.Contractor
import com.nammaraste.health.data.db.entity.DamageReport
import com.nammaraste.health.data.db.entity.Road
import java.util.concurrent.TimeUnit

/**
 * Seeds the database with realistic demo data for Karnataka rural roads.
 * Includes 5 contractors, 12 roads across 4 talukas, and 30+ damage reports.
 */
object SeedData {

    suspend fun seedAll(db: AppDatabase) {
        val contractorIds = seedContractors(db)
        val roadIds = seedRoads(db, contractorIds)
        seedReports(db, roadIds)
    }

    // ─── Contractors ──────────────────────────────────────────────────────────

    private suspend fun seedContractors(db: AppDatabase): List<Long> {
        val contractors = listOf(
            Contractor(
                name = "Shree Sai Infratech Pvt Ltd",
                contactPerson = "Ramesh Naik",
                phone = "+91 98450 11234",
                email = "ramesh@shreesaiinfra.com",
                address = "Plot 23, Industrial Area, Dharwad, Karnataka 580001",
                registrationNumber = "KA-GIN-2018-04521",
                specialisation = "Road Construction & Bituminous Works",
                rating = 4.2f,
                projectsCompleted = 47
            ),
            Contractor(
                name = "BMS Constructions",
                contactPerson = "Bhimappa Suttar",
                phone = "+91 94483 56789",
                email = "info@bmsconstructions.in",
                address = "MG Road, Belagavi, Karnataka 590001",
                registrationNumber = "KA-GIN-2015-02890",
                specialisation = "Rural Road Development & Bridges",
                rating = 3.8f,
                projectsCompleted = 62
            ),
            Contractor(
                name = "Karnataka Road Works",
                contactPerson = "Suresh Patil",
                phone = "+91 99016 78901",
                email = "suresh@karnatakardworks.com",
                address = "Station Road, Vijayapura, Karnataka 586101",
                registrationNumber = "KA-GIN-2012-01456",
                specialisation = "Concrete Road & Drainage Works",
                rating = 4.5f,
                projectsCompleted = 89
            ),
            Contractor(
                name = "Green Build Infra",
                contactPerson = "Asha Reddy",
                phone = "+91 80501 23456",
                email = "asha@greenbuildinfra.com",
                address = "IT Park Road, Hubballi, Karnataka 580029",
                registrationNumber = "KA-GIN-2019-07823",
                specialisation = "Eco-friendly Road Construction",
                rating = 4.0f,
                projectsCompleted = 28
            ),
            Contractor(
                name = "Deccan Highway Solutions",
                contactPerson = "Manjunath Rao",
                phone = "+91 96116 34567",
                email = "manju@deccanhs.co.in",
                address = "Commerce Circle, Mysuru, Karnataka 570001",
                registrationNumber = "KA-GIN-2010-00234",
                specialisation = "Highway & Rural Road Maintenance",
                rating = 3.5f,
                projectsCompleted = 115
            )
        )
        val ids = mutableListOf<Long>()
        contractors.forEach { ids.add(db.contractorDao().insertContractor(it)) }
        return ids
    }

    // ─── Roads ────────────────────────────────────────────────────────────────

    private suspend fun seedRoads(db: AppDatabase, contractorIds: List<Long>): List<Long> {
        val now = System.currentTimeMillis()
        val oneYear = TimeUnit.DAYS.toMillis(365)

        val roads = listOf(
            Road(
                name = "Dharwad–Kalaghatagi MDR",
                taluka = "Dharwad",
                district = "Dharwad",
                lengthKm = 18.4,
                surfaceType = "Asphalt",
                contractorId = contractorIds[0],
                warrantyExpiryMs = now + 2 * oneYear,
                constructionYear = 2022,
                startLat = 15.4589, startLng = 75.0078,
                endLat = 15.2033, endLng = 74.9734
            ),
            Road(
                name = "Kundgol–Navalgund Village Road",
                taluka = "Dharwad",
                district = "Dharwad",
                lengthKm = 9.2,
                surfaceType = "Gravel",
                contractorId = contractorIds[3],
                warrantyExpiryMs = now + oneYear,
                constructionYear = 2021,
                startLat = 15.2566, startLng = 75.2520,
                endLat = 15.5680, endLng = 75.3690
            ),
            Road(
                name = "Belagavi–Gokak State Highway",
                taluka = "Belagavi",
                district = "Belagavi",
                lengthKm = 54.1,
                surfaceType = "Asphalt",
                contractorId = contractorIds[1],
                warrantyExpiryMs = now + 3 * oneYear,
                constructionYear = 2023,
                startLat = 15.8497, startLng = 74.4977,
                endLat = 16.1700, endLng = 74.8233
            ),
            Road(
                name = "Ramadurga Link Road",
                taluka = "Belagavi",
                district = "Belagavi",
                lengthKm = 6.7,
                surfaceType = "Concrete",
                contractorId = contractorIds[2],
                warrantyExpiryMs = now + 4 * oneYear,
                constructionYear = 2024,
                startLat = 16.0023, startLng = 74.7134,
                endLat = 16.0912, endLng = 74.6821
            ),
            Road(
                name = "Vijayapura–Sindagi MDR",
                taluka = "Vijayapura",
                district = "Vijayapura",
                lengthKm = 42.5,
                surfaceType = "Asphalt",
                contractorId = contractorIds[4],
                warrantyExpiryMs = now - oneYear / 2, // expired
                constructionYear = 2019,
                startLat = 16.8302, startLng = 75.7100,
                endLat = 16.9235, endLng = 76.2380
            ),
            Road(
                name = "Indi–Muddebihal Rural Road",
                taluka = "Vijayapura",
                district = "Vijayapura",
                lengthKm = 23.0,
                surfaceType = "Gravel",
                contractorId = contractorIds[1],
                warrantyExpiryMs = now + oneYear / 3,
                constructionYear = 2020,
                startLat = 17.1734, startLng = 75.9605,
                endLat = 16.3391, endLng = 76.1302
            ),
            Road(
                name = "Hubballi Ring Road Extension",
                taluka = "Hubballi",
                district = "Dharwad",
                lengthKm = 11.8,
                surfaceType = "Concrete",
                contractorId = contractorIds[2],
                warrantyExpiryMs = now + 5 * oneYear,
                constructionYear = 2025,
                startLat = 15.3647, startLng = 75.1240,
                endLat = 15.4200, endLng = 75.0300
            ),
            Road(
                name = "Kalagatagi–Annigeri Panchayat Road",
                taluka = "Hubballi",
                district = "Dharwad",
                lengthKm = 14.3,
                surfaceType = "Asphalt",
                contractorId = contractorIds[0],
                warrantyExpiryMs = now + oneYear,
                constructionYear = 2022,
                startLat = 15.2033, startLng = 74.9734,
                endLat = 15.4230, endLng = 75.4230
            ),
            Road(
                name = "Nargund–Shirhatti MDR",
                taluka = "Gadag",
                district = "Gadag",
                lengthKm = 31.6,
                surfaceType = "Asphalt",
                contractorId = contractorIds[4],
                warrantyExpiryMs = now - oneYear, // expired
                constructionYear = 2018,
                startLat = 15.7234, startLng = 75.3890,
                endLat = 15.2300, endLng = 75.6430
            ),
            Road(
                name = "Gadag–Ron Link Road",
                taluka = "Gadag",
                district = "Gadag",
                lengthKm = 19.1,
                surfaceType = "Concrete",
                contractorId = contractorIds[3],
                warrantyExpiryMs = now + 2 * oneYear,
                constructionYear = 2023,
                startLat = 15.4322, startLng = 75.6267,
                endLat = 15.7010, endLng = 75.7120
            ),
            Road(
                name = "Mundargi–Lakshmeshwar Village Road",
                taluka = "Gadag",
                district = "Gadag",
                lengthKm = 8.9,
                surfaceType = "Gravel",
                contractorId = contractorIds[1],
                warrantyExpiryMs = now + oneYear / 2,
                constructionYear = 2021,
                startLat = 15.1897, startLng = 75.8843,
                endLat = 15.1300, endLng = 75.4670
            ),
            Road(
                name = "Saundatti–Savadatti Rural Road",
                taluka = "Belagavi",
                district = "Belagavi",
                lengthKm = 16.5,
                surfaceType = "Asphalt",
                contractorId = contractorIds[2],
                warrantyExpiryMs = now + 3 * oneYear,
                constructionYear = 2024,
                startLat = 15.7744, startLng = 75.1167,
                endLat = 15.8100, endLng = 74.9680
            )
        )

        val ids = mutableListOf<Long>()
        roads.forEach { ids.add(db.roadDao().insertRoad(it)) }
        return ids
    }

    // ─── Damage Reports ───────────────────────────────────────────────────────

    private suspend fun seedReports(db: AppDatabase, roadIds: List<Long>) {
        val now = System.currentTimeMillis()
        val day = TimeUnit.DAYS.toMillis(1)

        val reports = listOf(
            // Road 0 - Dharwad–Kalaghatagi (2 reports)
            DamageReport(roadId = roadIds[0], photoPath = null, issueType = "POTHOLE",
                severity = "HIGH", description = "Large pothole causing vehicle damage near km 4",
                latitude = 15.4200, longitude = 75.0050, timestamp = now - 3 * day, status = "ACKNOWLEDGED"),
            DamageReport(roadId = roadIds[0], photoPath = null, issueType = "CRACK",
                severity = "MEDIUM", description = "Longitudinal cracks visible over 50m stretch",
                latitude = 15.3900, longitude = 74.9900, timestamp = now - 7 * day, status = "PENDING"),

            // Road 1 - Kundgol–Navalgund (1 report)
            DamageReport(roadId = roadIds[1], photoPath = null, issueType = "WATERLOGGING",
                severity = "HIGH", description = "Road flooded after rain, 200m stretch impassable",
                latitude = 15.3100, longitude = 75.2800, timestamp = now - day, status = "PENDING"),

            // Road 2 - Belagavi–Gokak (5 reports)
            DamageReport(roadId = roadIds[2], photoPath = null, issueType = "POTHOLE",
                severity = "CRITICAL", description = "Deep pothole 1m wide, danger to two-wheelers",
                latitude = 15.9000, longitude = 74.6000, timestamp = now - 2 * day, status = "PENDING"),
            DamageReport(roadId = roadIds[2], photoPath = null, issueType = "CRACK",
                severity = "HIGH", description = "Network of alligator cracks near km 12",
                latitude = 15.9500, longitude = 74.6500, timestamp = now - 5 * day, status = "PENDING"),
            DamageReport(roadId = roadIds[2], photoPath = null, issueType = "POTHOLE",
                severity = "MEDIUM", description = "Multiple small potholes km 18–20",
                latitude = 16.0100, longitude = 74.7200, timestamp = now - 8 * day, status = "ACKNOWLEDGED"),
            DamageReport(roadId = roadIds[2], photoPath = null, issueType = "DEBRIS",
                severity = "LOW", description = "Mud and stone debris from road side",
                latitude = 16.0800, longitude = 74.7800, timestamp = now - 10 * day, status = "RESOLVED"),
            DamageReport(roadId = roadIds[2], photoPath = null, issueType = "WATERLOGGING",
                severity = "HIGH", description = "Broken drainage causing water to stagnate on road",
                latitude = 16.1200, longitude = 74.8000, timestamp = now - 12 * day, status = "IN_PROGRESS"),

            // Road 3 - Ramadurga (1 report)
            DamageReport(roadId = roadIds[3], photoPath = null, issueType = "CRACK",
                severity = "LOW", description = "Minor surface cracks near the culvert",
                latitude = 16.0400, longitude = 74.6900, timestamp = now - 4 * day, status = "PENDING"),

            // Road 4 - Vijayapura–Sindagi (6 reports - poor health)
            DamageReport(roadId = roadIds[4], photoPath = null, issueType = "POTHOLE",
                severity = "CRITICAL", description = "Massive pothole at entry of Sindagi town",
                latitude = 16.8600, longitude = 75.8000, timestamp = now - day, status = "PENDING"),
            DamageReport(roadId = roadIds[4], photoPath = null, issueType = "POTHOLE",
                severity = "CRITICAL", description = "Row of potholes km 8–11, road nearly unusable",
                latitude = 16.8800, longitude = 75.9200, timestamp = now - 2 * day, status = "PENDING"),
            DamageReport(roadId = roadIds[4], photoPath = null, issueType = "CRACK",
                severity = "HIGH", description = "Wide cracks exposing sub-base material",
                latitude = 16.8900, longitude = 76.0000, timestamp = now - 3 * day, status = "PENDING"),
            DamageReport(roadId = roadIds[4], photoPath = null, issueType = "WATERLOGGING",
                severity = "HIGH", description = "Entire 500m stretch flooded, no drainage",
                latitude = 16.9000, longitude = 76.1000, timestamp = now - 4 * day, status = "ACKNOWLEDGED"),
            DamageReport(roadId = roadIds[4], photoPath = null, issueType = "DEBRIS",
                severity = "MEDIUM", description = "Construction debris blocking half the road",
                latitude = 16.9100, longitude = 76.1500, timestamp = now - 6 * day, status = "PENDING"),
            DamageReport(roadId = roadIds[4], photoPath = null, issueType = "POTHOLE",
                severity = "HIGH", description = "Road surface completely broken near km 32",
                latitude = 16.9200, longitude = 76.2000, timestamp = now - 9 * day, status = "PENDING"),

            // Road 5 - Indi–Muddebihal (3 reports)
            DamageReport(roadId = roadIds[5], photoPath = null, issueType = "CRACK",
                severity = "MEDIUM", description = "Transverse cracking every 10m",
                latitude = 17.1000, longitude = 75.9800, timestamp = now - 5 * day, status = "PENDING"),
            DamageReport(roadId = roadIds[5], photoPath = null, issueType = "POTHOLE",
                severity = "HIGH", description = "Pothole cluster near village junction",
                latitude = 16.8000, longitude = 76.0500, timestamp = now - 7 * day, status = "PENDING"),
            DamageReport(roadId = roadIds[5], photoPath = null, issueType = "WATERLOGGING",
                severity = "MEDIUM", description = "Water seeping through road surface",
                latitude = 16.6000, longitude = 76.1000, timestamp = now - 11 * day, status = "ACKNOWLEDGED"),

            // Road 6 - Hubballi Ring Road (0 reports — new road, healthy)

            // Road 7 - Kalagatagi–Annigeri (2 reports)
            DamageReport(roadId = roadIds[7], photoPath = null, issueType = "CRACK",
                severity = "LOW", description = "Minor edge cracking",
                latitude = 15.3000, longitude = 75.1500, timestamp = now - 14 * day, status = "RESOLVED"),
            DamageReport(roadId = roadIds[7], photoPath = null, issueType = "POTHOLE",
                severity = "MEDIUM", description = "Shallow pothole near speed bump",
                latitude = 15.3500, longitude = 75.2500, timestamp = now - 20 * day, status = "RESOLVED"),

            // Road 8 - Nargund–Shirhatti (4 reports - old road)
            DamageReport(roadId = roadIds[8], photoPath = null, issueType = "POTHOLE",
                severity = "HIGH", description = "Road badly damaged near Nargund junction",
                latitude = 15.7000, longitude = 75.4000, timestamp = now - 3 * day, status = "PENDING"),
            DamageReport(roadId = roadIds[8], photoPath = null, issueType = "CRACK",
                severity = "HIGH", description = "Alligator cracking km 5–8",
                latitude = 15.6000, longitude = 75.4500, timestamp = now - 6 * day, status = "PENDING"),
            DamageReport(roadId = roadIds[8], photoPath = null, issueType = "POTHOLE",
                severity = "CRITICAL", description = "Road fully collapsed at one section",
                latitude = 15.5000, longitude = 75.5000, timestamp = now - 9 * day, status = "IN_PROGRESS"),
            DamageReport(roadId = roadIds[8], photoPath = null, issueType = "DEBRIS",
                severity = "MEDIUM", description = "Loose gravel all over surface",
                latitude = 15.4000, longitude = 75.5500, timestamp = now - 13 * day, status = "PENDING"),

            // Road 9 - Gadag–Ron (1 report)
            DamageReport(roadId = roadIds[9], photoPath = null, issueType = "CRACK",
                severity = "LOW", description = "Hairline cracks on concrete surface",
                latitude = 15.5000, longitude = 75.6700, timestamp = now - 8 * day, status = "ACKNOWLEDGED"),

            // Road 10 - Mundargi–Lakshmeshwar (2 reports)
            DamageReport(roadId = roadIds[10], photoPath = null, issueType = "WATERLOGGING",
                severity = "HIGH", description = "No drainage, road damaged by runoff",
                latitude = 15.1600, longitude = 75.7000, timestamp = now - 2 * day, status = "PENDING"),
            DamageReport(roadId = roadIds[10], photoPath = null, issueType = "POTHOLE",
                severity = "MEDIUM", description = "Several potholes along gravel stretch",
                latitude = 15.1400, longitude = 75.5500, timestamp = now - 5 * day, status = "PENDING"),

            // Road 11 - Saundatti–Savadatti (1 report)
            DamageReport(roadId = roadIds[11], photoPath = null, issueType = "CRACK",
                severity = "LOW", description = "Minor surface cracking, new road",
                latitude = 15.7900, longitude = 75.0500, timestamp = now - 15 * day, status = "RESOLVED")
        )

        db.reportDao().insertAll(reports)
    }
}
