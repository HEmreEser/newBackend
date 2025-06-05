class Review {
  final int id;
  final int userId;
  final String userName;
  final int itemId;
  final int rentalId;
  final int rating;
  final String? comment;
  final DateTime createdAt;
  final DateTime? updatedAt;

  Review({
    required this.id,
    required this.userId,
    required this.userName,
    required this.itemId,
    required this.rentalId,
    required this.rating,
    this.comment,
    required this.createdAt,
    this.updatedAt,
  });

  factory Review.fromJson(Map<String, dynamic> json) {
    return Review(
      id: json['id'],
      userId: json['userId'],
      userName: json['userName'] ?? 'Unbekannt',
      itemId: json['itemId'],
      rentalId: json['rentalId'],
      rating: json['rating'],
      comment: json['comment'],
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt:
          json['updatedAt'] != null ? DateTime.parse(json['updatedAt']) : null,
    );
  }
}
