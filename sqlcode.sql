-- Tạo cơ sở dữ liệu
CREATE DATABASE MovieTicketBooking;
GO

USE MovieTicketBooking;
GO

-- Bảng Customer
CREATE TABLE Customer (
    CustomerID INT PRIMARY KEY IDENTITY(1,1),
    Username NVARCHAR(50) NOT NULL UNIQUE,
    Password NVARCHAR(100) NOT NULL,
    FullName NVARCHAR(100),
    Email NVARCHAR(100)
);
GO

-- Bảng Movie
CREATE TABLE Movie (
    MovieID INT PRIMARY KEY IDENTITY(1,1),
    Title NVARCHAR(100) NOT NULL,
    Description NVARCHAR(500),
    Poster NVARCHAR(200)
);
GO
-- Create Genre table if not exists
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Genre]') AND type in (N'U'))
BEGIN
CREATE TABLE Genre (
                       GenreID INT PRIMARY KEY IDENTITY(1,1),
                       GenreName NVARCHAR(50) NOT NULL
);
END;

-- Create Country table if not exists
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Country]') AND type in (N'U'))
BEGIN
CREATE TABLE Country (
                         CountryID INT PRIMARY KEY IDENTITY(1,1),
                         CountryName NVARCHAR(100) NOT NULL
);
END;

-- Alter Movie table to add new columns
ALTER TABLE Movie
    ADD
        Duration INT,
    Director NVARCHAR(100),
    GenreID INT,
    StartDate DATE,
    EndDate DATE,
    ProductionYear INT,
    CountryID INT;

-- Add foreign key constraints
ALTER TABLE Movie
    ADD CONSTRAINT FK_Movie_Genre FOREIGN KEY (GenreID) REFERENCES Genre(GenreID);

ALTER TABLE Movie
    ADD CONSTRAINT FK_Movie_Country FOREIGN KEY (CountryID) REFERENCES Country(CountryID);

-- Insert sample data into Genre and Country tables
INSERT INTO Genre (GenreName) VALUES
                                  (N'Hành động'),
                                  (N'Tình cảm'),
                                  (N'Kinh dị'),
                                  (N'Hài'),
                                  (N'Khoa học viễn tưởng');

INSERT INTO Country (CountryName) VALUES
                                      (N'Việt Nam'),
                                      (N'Mỹ'),
                                      (N'Hàn Quốc'),
                                      (N'Nhật Bản'),
                                      (N'Trung Quốc');
GO

-- Update existing movie (Phim A) with new columns
UPDATE Movie
SET
    Duration = 120,
    Director = N'Nguyễn Văn A',
    GenreID = (SELECT GenreID FROM Genre WHERE GenreName = N'Hành động'),
    StartDate = '2025-01-01',
    EndDate = '2025-03-01',
    ProductionYear = 2024,
    CountryID = (SELECT CountryID FROM Country WHERE CountryName = N'Việt Nam')
WHERE MovieID = 1;

-- Insert new sample movies
INSERT INTO Movie (Title, Description, Duration, Director, GenreID, Poster, StartDate, EndDate, ProductionYear, CountryID) VALUES
                                                                                                                               (N'Phim B', N'Một bộ phim tình cảm đầy cảm xúc', 110, N'Trần Thị B',
-- Alter the Movie table to add AgeRestriction and modify Description
                                                                                                                                   ALTER TABLE Movie
                                                                                                                                ADD AgeRestriction INT;

ALTER TABLE Movie
ALTER COLUMN Description NVARCHAR(MAX);

-- Insert a sample movie with the new AgeRestriction attribute
INSERT INTO Movie (Title, Description, Duration, Director, GenreID, Poster, StartDate, EndDate, ProductionYear, CountryID, AgeRestriction) VALUES
    (N'Phim Mẫu',
     N'Đây là một bộ phim mẫu với mô tả rất dài để kiểm tra việc không giới hạn ký tự. Bộ phim kể về một cuộc phiêu lưu kỳ thú của một nhóm bạn trẻ trong một thế giới giả tưởng, nơi họ phải đối mặt với những thử thách lớn lao và khám phá những bí ẩn cổ xưa. Cốt truyện đầy cảm xúc và bất ngờ, phù hợp cho mọi lứa tuổi nhưng có giới hạn độ tuổi nhất định để đảm bảo nội dung phù hợp.',
     135,
     N'Đạo Diễn Mẫu',
     (SELECT GenreID FROM Genre WHERE GenreName = N'Khoa học viễn tưởng'),
     'path/to/sample_poster.jpg',
     '2025-06-01',
     '2025-08-01',
     2025,
     (SELECT CountryID FROM Country WHERE CountryName = N'Việt Nam'),
     16);
GO                                                                                                                              (SELECT GenreID FROM Genre WHERE GenreName = N'Tình cảm'),
                                                                                                                                'path/to/posterB.jpg', '2025-02-01', '2025-04-01', 2025,
                                                                                                                                (SELECT CountryID FROM Country WHERE CountryName = N'Hàn Quốc')),
                                                                                                                               (N'Phim C', N'Bộ phim kinh dị rùng rợn', 95, N'Lê Văn C',
                                                                                                                                (SELECT GenreID FROM Genre WHERE GenreName = N'Kinh dị'),
                                                                                                                                'path/to/posterC.jpg', '2025-03-01', '2025-05-01', 2025,
                                                                                                                                (SELECT CountryID FROM Country WHERE CountryName = N'Mỹ')),
                                                                                                                               (N'Phim D', N'Phim hài vui nhộn', 100, N'Phạm Thị D',
                                                                                                                                (SELECT GenreID FROM Genre WHERE GenreName = N'Hài'),
                                                                                                                                'path/to/posterD.jpg', '2025-04-01', '2025-06-01', 2024,
                                                                                                                                (SELECT CountryID FROM Country WHERE CountryName = N'Nhật Bản')),
                                                                                                                               (N'Phim E', N'Hành trình khoa học viễn tưởng', 130, N'Hoàng Văn E',
                                                                                                                                (SELECT GenreID FROM Genre WHERE GenreName = N'Khoa học viễn tưởng'),
                                                                                                                                'path/to/posterE.jpg', '2025-05-01', '2025-07-01', 2025,
                                                                                                                                (SELECT CountryID FROM Country WHERE CountryName = N'Trung Quốc'));
GO
-- Bảng Room
CREATE TABLE Room (
    RoomID INT PRIMARY KEY IDENTITY(1,1),
    RoomName NVARCHAR(50) NOT NULL,
    Capacity INT NOT NULL
);
GO

-- Bảng Showtime
CREATE TABLE Showtime (
    ShowtimeID INT PRIMARY KEY IDENTITY(1,1),
    MovieID INT NOT NULL,
    RoomID INT NOT NULL,
    ShowDate DATETIME NOT NULL,
    FOREIGN KEY (MovieID) REFERENCES Movie(MovieID),
    FOREIGN KEY (RoomID) REFERENCES Room(RoomID)
);
GO

-- Bảng Ticket
CREATE TABLE Ticket (
    TicketID INT PRIMARY KEY IDENTITY(1,1),
    CustomerID INT NOT NULL,
    ShowtimeID INT NOT NULL,
    Seat NVARCHAR(10) NOT NULL,
    Price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID),
    FOREIGN KEY (ShowtimeID) REFERENCES Showtime(ShowtimeID)
);
GO

-- Bảng Revenue
CREATE TABLE Revenue (
    RevenueID INT PRIMARY KEY IDENTITY(1,1),
    ShowtimeID INT NOT NULL,
    TotalRevenue DECIMAL(10, 2) NOT NULL,
    RevenueDate DATE NOT NULL,
    FOREIGN KEY (ShowtimeID) REFERENCES Showtime(ShowtimeID)
);
GO

-- Thêm dữ liệu mẫu
INSERT INTO Customer (Username, Password, FullName, Email) 
VALUES ('user1', 'hashed_password', 'Nguyen Van A', 'user1@email.com');
GO

INSERT INTO Movie (Title, Description, Poster) 
VALUES ('Phim A', 'Mô tả phim A', 'posterA.jpg');
GO

INSERT INTO Room (RoomName, Capacity) 
VALUES ('Phong 1', 100);
GO

INSERT INTO Showtime (MovieID, RoomID, ShowDate) 
VALUES (1, 1, '2025-05-17 19:00:00');
GO




-- Sửa bảng Room để thêm cột Price
ALTER TABLE Room
    ADD Price DECIMAL(10, 2);

-- Thêm bảng Seat để lưu trạng thái ghế
CREATE TABLE Seat (
                      SeatID INT PRIMARY KEY IDENTITY(1,1),
                      RoomID INT NOT NULL,
                      SeatNumber NVARCHAR(10) NOT NULL, -- Ví dụ: A1, A2
                      Status NVARCHAR(20) NOT NULL DEFAULT 'Trống', -- Trống/Đã đặt
                      FOREIGN KEY (RoomID) REFERENCES Room(RoomID)
);

-- Sửa bảng Ticket để liên kết với Seat thay vì cột Seat
ALTER TABLE Ticket
DROP COLUMN Seat;

ALTER TABLE Ticket
    ADD SeatID INT NOT NULL;

ALTER TABLE Ticket
    ADD CONSTRAINT FK_Ticket_Seat FOREIGN KEY (SeatID) REFERENCES Seat(SeatID);

-- Thêm bảng BookingHistory để lưu lịch sử đặt vé
CREATE TABLE BookingHistory (
                                HistoryID INT PRIMARY KEY IDENTITY(1,1),
                                CustomerID INT NOT NULL,
                                TicketID INT NOT NULL,
                                BookingDate DATETIME NOT NULL,
                                MovieTitle NVARCHAR(100) NOT NULL,
                                RoomName NVARCHAR(50) NOT NULL,
                                SeatNumber NVARCHAR(10) NOT NULL,
                                Price DECIMAL(10, 2) NOT NULL,
                                FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID),
                                FOREIGN KEY (TicketID) REFERENCES Ticket(TicketID)
);

-- Thêm dữ liệu mẫu cho Room (cập nhật Price)
UPDATE Room
SET Price = 100000
WHERE RoomID = 1;

-- Thêm dữ liệu mẫu cho Seat (giả sử Phòng 1 có 10 ghế)
INSERT INTO Seat (RoomID, SeatNumber, Status) VALUES
                                                  (1, 'A1', 'Trống'),
                                                  (1, 'A2', 'Trống'),
                                                  (1, 'A3', 'Trống'),
                                                  (1, 'A4', 'Trống'),
                                                  (1, 'A5', 'Trống'),
                                                  (1, 'B1', 'Trống'),
                                                  (1, 'B2', 'Trống'),
                                                  (1, 'B3', 'Trống'),
                                                  (1, 'B4', 'Trống'),
                                                  (1, 'B5', 'Trống');

-- Thêm dữ liệu mẫu cho Showtime (để kiểm tra trạng thái phòng)
INSERT INTO Showtime (MovieID, RoomID, ShowDate) VALUES
                                                     (2, 1, '2025-05-17 20:00:00'), -- Đang chiếu (trong khoảng thời gian hiện tại)
                                                     (3, 1, '2025-05-18 10:00:00'); -- Chuẩn bị chiếu
GO