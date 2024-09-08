# backend
#io.vertx.core.Launcher
#java 21.0.4

Mã lỗi 2xx (Success) - Thành công
200 OK: Yêu cầu đã được xử lý thành công. Kết quả trả về (nếu có) được đính kèm trong phần body của phản hồi.
201 Created: Yêu cầu đã thành công và một tài nguyên mới đã được tạo. Địa chỉ của tài nguyên mới được cung cấp trong trường Location của phản hồi.
202 Accepted: Yêu cầu đã được chấp nhận để xử lý, nhưng chưa hoàn thành. Kết quả cuối cùng sẽ có sau.
204 No Content: Yêu cầu đã được xử lý thành công nhưng không có nội dung nào để trả về.

Mã lỗi 3xx (Redirection) - Chuyển hướng
301 Moved Permanently: Tài nguyên được yêu cầu đã được di chuyển vĩnh viễn đến URL mới. Trình duyệt sẽ tự động chuyển hướng đến URL mới.
302 Found: Tài nguyên được yêu cầu tạm thời nằm ở URL khác. Trình duyệt có thể chuyển hướng tới URL mới, nhưng URL cũ vẫn được sử dụng trong tương lai.
304 Not Modified: Tài nguyên chưa thay đổi kể từ lần truy cập cuối cùng. Trình duyệt có thể sử dụng phiên bản cache.

Mã lỗi 4xx (Client Error) - Lỗi phía khách hàng
400 Bad Request: Máy chủ không thể xử lý yêu cầu do cú pháp không hợp lệ. Có thể do dữ liệu gửi lên không đúng định dạng.
401 Unauthorized: Yêu cầu chưa được xác thực hoặc thông tin xác thực không hợp lệ. Thường cần phải cung cấp thông tin đăng nhập để truy cập tài nguyên.
403 Forbidden: Máy chủ hiểu yêu cầu nhưng từ chối thực hiện. Người dùng không có quyền truy cập tài nguyên.
404 Not Found: Tài nguyên được yêu cầu không tồn tại trên máy chủ.
405 Method Not Allowed: Phương thức yêu cầu không được hỗ trợ đối với tài nguyên được yêu cầu (ví dụ: dùng POST cho tài nguyên chỉ chấp nhận GET).
409 Conflict: Xung đột với trạng thái hiện tại của tài nguyên, ví dụ như cập nhật trùng lặp hoặc xung đột trong quá trình đồng bộ dữ liệu.

Mã lỗi 5xx (Server Error) - Lỗi phía máy chủ
500 Internal Server Error: Máy chủ gặp lỗi không xác định trong quá trình xử lý yêu cầu. Đây là lỗi tổng quát nhất cho các lỗi không rõ nguồn gốc.
501 Not Implemented: Máy chủ không hỗ trợ phương thức hoặc chức năng được yêu cầu.
502 Bad Gateway: Máy chủ nhận được phản hồi không hợp lệ từ máy chủ ngược dòng (upstream server).
503 Service Unavailable: Máy chủ tạm thời không thể xử lý yêu cầu, thường do quá tải hoặc bảo trì.
504 Gateway Timeout: Máy chủ không nhận được phản hồi kịp thời từ máy chủ ngược dòng, dẫn đến hết thời gian chờ.