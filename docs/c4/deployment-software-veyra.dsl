workspace "Veyra Platform" "Production deployment architecture — IoT healthcare monitoring platform." {

    !identifiers hierarchical

    model {

        veyra = softwareSystem "Veyra Platform" "IoT-based healthcare monitoring for nursing homes." {

            # Frontends
            landingPage = container "Landing Page" \
                "Static marketing site with app-store redirects for mobile visitors." \
                "HTML / CSS / JavaScript" \
                "LandingPage"

            webApp = container "Web Application" \
                "Delivers the compiled Angular SPA bundle to the browser." \
                "Nginx" \
                "WebFrontend"

            webClient = container "Veyra Web Client" \
                "Angular SPA for doctors, staff, and administrators. REST + WebSocket." \
                "TypeScript / Angular" \
                "Browser"

            mobileApp = container "Veyra Mobile Application" \
                "Flutter app for relatives and healthcare staff. Push notifications + offline cache." \
                "Flutter / Dart" \
                "MobileApp"

            # Backend
            nginx = container "Proxy (Nginx)" \
                "Reverse proxy. TLS termination, rate limiting via Redis, routes to API." \
                "Nginx" \
                "Proxy"

            api = container "Veyra API Application" \
                "Spring Boot monolith. REST API, all bounded contexts, WebSocket/SSE streaming, RabbitMQ integration." \
                "Java / Spring Boot" \
                "ServerApp"

            # Messaging
            mqttBroker = container "MQTT Broker" \
                "Receives firmware telemetry. Topics: veyra/vitals/{id}, veyra/gps/{id}. On-premises, cloud-independent." \
                "Eclipse Mosquitto / EMQX" \
                "MessageBroker"

            messageBroker = container "Message Broker" \
                "RabbitMQ domain-event bus. Durable queues. Publishers: IAM, Health, Tracking, Subscription. Subscribers: Communication, Realtime, Analytics." \
                "RabbitMQ 3" \
                "MessageBroker"

           redis = container "Redis" \
                "JWT blocklist (IAM), rate-limit counters (Nginx), application cache (API). NOT used as pub/sub." \
                "Redis 7" \
                "Cache"

            # Databases
            mysqlDb = container "MySQL Database" \
                "Users, roles, care plans, staff, subscriptions, payment transactions, alert thresholds." \
                "MySQL 8" \
                "Database"

            mongoDb = container "MongoDB Database" \
                "Vital signs time-series, GPS history, geofence definitions and breach records." \
                "MongoDB 7" \
                "Database"

            sqliteDb = container "SQLite" \
                "Offline-first cache on mobile device. Resident summaries and queued participation records." \
                "SQLite" \
                "Database"

            sqlServerDb = container "SQL Server" \
                "Edge telemetry buffer. Persists batches when cloud uplink is unavailable." \
                "Microsoft SQL Server" \
                "Database"

            # Edge & IoT
            edgeApp = container "Veyra Edge Application" \
                "Subscribes to MQTT topics, aggregates and filters telemetry, buffers in SQL Server, forwards to API." \
                "Python" \
                "Edge"

            embeddedVital = container "Vital Signs Firmware" \
                "Acquires HR, SpO2, temperature, blood pressure. Publishes to veyra/vitals/{deviceId}." \
                "C++ Firmware" \
                "IoT"

            embeddedGPS = container "GPS Firmware" \
                "Parses GNSS NMEA sentences. Publishes coordinates to veyra/gps/{deviceId}." \
                "C++ Firmware" \
                "IoT"

            # ── Relationships ─────────────────────────────────────────────────

            landingPage -> webApp    "Requests SPA bundle"                                       "HTTPS"
            webApp      -> webClient "Delivers SPA bundle"                                       "HTTPS"

            webClient   -> nginx     "REST API calls"                                            "REST / HTTPS"
            mobileApp   -> nginx     "REST API calls"                                            "REST / HTTPS"
            nginx       -> redis     "Rate-limit counter reads/increments"                       "Redis Protocol"
            nginx       -> api       "Proxies REST, WebSocket, SSE"                              "HTTP / WS / SSE"

            api         -> redis     "JWT blocklist writes/reads + application cache"            "Redis Protocol / TLS"
            api         -> mysqlDb   "Structured domain data"                                    "JDBC / JPA"
            api         -> mongoDb   "IoT time-series and geofence data"                         "MongoDB Driver"

            api           -> messageBroker "PUBLISHES domain events (HealthAlert, GeofenceBreach, UserRegistered, PaymentFailed)" "AMQP / TLS"

            embeddedVital -> mqttBroker  "PUBLISHES veyra/vitals/{deviceId}"                    "MQTT 3.1.1 / TLS"
            embeddedGPS   -> mqttBroker  "PUBLISHES veyra/gps/{deviceId}"                       "MQTT 3.1.1 / TLS"
            edgeApp       -> mqttBroker  "SUBSCRIBES to veyra/vitals/# and veyra/gps/#"         "MQTT 3.1.1 / TLS"
            edgeApp       -> sqlServerDb "Buffers telemetry batches"                             "JDBC / TDS"
            edgeApp       -> api         "Forwards vital signs and GPS batches"                  "REST / HTTPS"

            mobileApp -> sqliteDb "Offline cache read/write"                                     "SQLite API"

           }

        # ── Deployment Environment ────────────────────────────────────────────

        deploymentEnvironment "Production" {

            deploymentNode "Veyra Production Infrastructure" "" "Global Multi-Cloud and On-Premises" {
                tags "Wrapper"

                deploymentNode "Cloudflare" "" "CDN / Global Edge Network" {
                    tags "Cloudflare"

                    deploymentNode "Cloudflare Pages — Landing" "" "Static Hosting" {
                        containerInstance veyra.landingPage
                    }
                    deploymentNode "Cloudflare Pages — Web App" "" "Static Hosting" {
                        containerInstance veyra.webApp
                    }
                }

                deploymentNode "User Device" "" "Windows / macOS / Linux" {
                    tags "UserDevice"
                    deploymentNode "Web Browser" "" "Chrome / Firefox / Safari / Edge" {
                        containerInstance veyra.webClient
                    }
                }

                deploymentNode "Mobile Device" "" "iOS / Android" {
                    tags "MobileDevice"
                    deploymentNode "Flutter Application Runtime" "" "iOS App / Android App" {
                        containerInstance veyra.mobileApp
                        containerInstance veyra.sqliteDb
                    }
                }

                deploymentNode "Microsoft Azure" "" "Cloud Provider — Primary Region" {
                    tags "Azure"

                    deploymentNode "Azure App Service — Nginx Proxy" "" "PaaS / Custom Container" {
                        containerInstance veyra.nginx
                    }

                    deploymentNode "Azure App Service Plan" "" "PaaS — Auto-scaling Compute" {
                        deploymentNode "App Service Instance" "" "Azure App Service" {
                            containerInstance veyra.api
                        }
                    }

                    deploymentNode "Azure VM — RabbitMQ" "" "Standard_B2s / Ubuntu 24 LTS" {
                        containerInstance veyra.messageBroker
                    }

                    deploymentNode "Azure Managed Database Services" "" "Managed Database Platform" {

                        deploymentNode "Azure Database for MySQL — Flexible Server" "" "Zone Redundant" {
                            containerInstance veyra.mysqlDb
                        }
                        deploymentNode "Azure Cosmos DB (MongoDB API)" "" "Serverless" {
                            containerInstance veyra.mongoDb
                        }
                        deploymentNode "Azure Cache for Redis" "" "Premium Tier" {
                            containerInstance veyra.redis
                        }
                    }
                }

                deploymentNode "Nursing Home Environment" "" "On-premises Local Network — Private LAN" {
                    tags "OnPremises"

                    deploymentNode "MQTT Broker Server" "" "Linux ARM64 / x86_64" {
                        containerInstance veyra.mqttBroker
                    }
                    deploymentNode "Edge Server" "" "Linux ARM64 / x86_64" {
                        containerInstance veyra.edgeApp
                        containerInstance veyra.sqlServerDb
                    }
                    deploymentNode "Vital Signs IoT Device" "" "Embedded Hardware — ARM Cortex-M" {
                        containerInstance veyra.embeddedVital
                    }
                    deploymentNode "GPS Tracker Device" "" "Embedded Hardware — ARM Cortex-M" {
                        containerInstance veyra.embeddedGPS
                    }
                }
            }
        }
    }

    views {

        deployment veyra "Production" "Veyra-Platform-Production-Deployment" {
            include *
            autoLayout lr
            title "Veyra Platform — Production Deployment"
            description "C4 Deployment View — MQTT (on-premises), RabbitMQ (Azure VM), Redis (JWT + rate limiting + cache)."
        }

        styles {

            # Deployment node zones
            element "Deployment Node" {
                background #ffffff
                color #333333
                stroke #aaaaaa
                border dashed
            }
            element "Wrapper" {
                background #f8f8f8
                color #333333
                stroke #666666
                border dashed
            }
            element "Cloudflare" {
                background #fff7ed
                stroke #f97316
                color #7c2d12
                border dashed
            }
            element "Azure" {
                background #eff6ff
                stroke #3b82f6
                color #1e3a8a
                border dashed
            }
            element "UserDevice" {
                background #faf5ff
                stroke #9333ea
                color #4c1d95
                border dashed
            }
            element "MobileDevice" {
                background #fefce8
                stroke #eab308
                color #713f12
                border dashed
            }
            element "OnPremises" {
                background #f0fdf4
                stroke #16a34a
                color #14532d
                border dashed
            }

            # Container shapes
            element "LandingPage" {
                shape Folder
                background #6d28d9
                color #ffffff
                stroke #4c1d95
            }
            element "WebFrontend" {
                shape RoundedBox
                background #7c3aed
                color #ffffff
                stroke #4c1d95
            }
            element "Browser" {
                shape WebBrowser
                background #0f766e
                color #ffffff
                stroke #134e4a
            }
            element "MobileApp" {
                shape MobileDeviceLandscape
                background #92400e
                color #ffffff
                stroke #78350f
            }
            element "Proxy" {
                shape RoundedBox
                background #475569
                color #ffffff
                stroke #334155
            }
            element "ServerApp" {
                shape RoundedBox
                background #1e3a5f
                color #ffffff
                stroke #1e40af
            }
            element "MessageBroker" {
                shape Pipe
                background #d97706
                color #ffffff
                stroke #92400e
            }
            element "Cache" {
                shape Cylinder
                background #9d174d
                color #ffffff
                stroke #831843
            }
            element "Database" {
                shape Cylinder
                background #0369a1
                color #ffffff
                stroke #0c4a6e
            }
            element "Edge" {
                shape RoundedBox
                background #059669
                color #ffffff
                stroke #064e3b
            }
            element "IoT" {
                shape RoundedBox
                background #0284c7
                color #ffffff
                stroke #0c4a6e
            }
            element "Container" {
                background #438dd5
                color #ffffff
                shape RoundedBox
            }

            relationship "Relationship" {
                color #666666
                fontSize 10
                thickness 2
                dashed false
            }
        }
    }
}
