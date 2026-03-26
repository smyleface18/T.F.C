IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'tfc_db')
BEGIN
    CREATE DATABASE tfc_db;
END
GO