package com.movie.bus;

import com.movie.dao.ShowtimeDAO;
import com.movie.model.Showtime;
import com.movie.model.Movie;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class ShowtimeBUS {
    private ShowtimeDAO showtimeDAO = new ShowtimeDAO();
    private MovieBUS movieBUS = new MovieBUS();

    public void addShowtime(int movieID, int roomID, Date showDate, int staffID) throws SQLException {
        Showtime showtime = new Showtime();
        showtime.setMovieID(movieID);
        showtime.setRoomID(roomID);
        showtime.setShowDate(showDate);
        showtime.setStaffID(staffID);
        showtime.setStatus("Không chiếu"); // Mặc định khi thêm suất chiếu mới
        showtimeDAO.addShowtime(showtime);
    }

    public void updateShowtimeStatus(int showtimeID, String status) throws SQLException {
        showtimeDAO.updateShowtimeStatus(showtimeID, status);
    }

    public List<Showtime> getAllShowtimes() throws SQLException {
        List<Showtime> showtimes = showtimeDAO.getAllShowtimes();
        updateShowtimeStatuses(showtimes);
        return showtimes;
    }

    // Thêm phương thức mới để lấy suất chiếu theo roomId và movieId
    public List<Showtime> getShowtimesByRoomAndMovie(int roomId, int movieId) throws SQLException {
        List<Showtime> showtimes = showtimeDAO.getAllShowtimes();
        List<Showtime> filteredShowtimes = showtimes.stream()
                .filter(showtime -> showtime.getRoomID() == roomId && showtime.getMovieID() == movieId)
                .toList();
        updateShowtimeStatuses(filteredShowtimes);
        return filteredShowtimes;
    }

    private void updateShowtimeStatuses(List<Showtime> showtimes) throws SQLException {
        long currentTime = System.currentTimeMillis(); // Thời gian hiện tại

        for (Showtime showtime : showtimes) {
            String currentStatus = showtime.getStatus();

            if (currentStatus != null && !currentStatus.equals("Ẩn")) {
                // Kiểm tra nếu thiếu phim hoặc ngày chiếu
                if (showtime.getMovieID() == 0 || showtime.getShowDate() == null) {
                    if (!currentStatus.equals("Không chiếu")) {
                        showtimeDAO.updateShowtimeStatus(showtime.getShowtimeID(), "Không chiếu");
                        showtime.setStatus("Không chiếu");
                    }
                    continue;
                }

                long showTime = showtime.getShowDate().getTime();
                Movie movie = movieBUS.getMovieById(showtime.getMovieID());

                // Nếu không tìm thấy phim hoặc thời lượng <= 0 thì bỏ qua cập nhật
                if (movie == null || movie.getDuration() <= 0) {
                    continue;
                }

                long duration = movie.getDuration() * 60 * 1000L; // phút → milliseconds

                String newStatus;
                if (currentTime < showTime) {
                    newStatus = "Sắp công chiếu";
                } else if (currentTime >= showTime && currentTime < showTime + duration) {
                    newStatus = "Đang chiếu";
                } else {
                    newStatus = "Đã chiếu xong";
                }

                // Nếu khác trạng thái hiện tại → cập nhật
                if (!newStatus.equals(currentStatus)) {
                    showtimeDAO.updateShowtimeStatus(showtime.getShowtimeID(), newStatus);
                    showtime.setStatus(newStatus);
                }
            }
        }
    }
}