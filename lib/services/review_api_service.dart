import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:kreisel_frontend/models/review_model.dart';

class ReviewApiService {
  static const String baseUrl = 'http://localhost:8080/api';

  // Alle Reviews für ein Item abrufen
  static Future<List<Review>> getItemReviews(int itemId) async {
    final response = await http.get(Uri.parse('$baseUrl/reviews/item/$itemId'));

    if (response.statusCode == 200) {
      List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => Review.fromJson(json)).toList();
    } else {
      throw Exception('Fehler beim Laden der Bewertungen');
    }
  }

  // Durchschnittsbewertung für ein Item abrufen
  static Future<Map<String, dynamic>> getItemRatingStats(int itemId) async {
    final response = await http.get(
      Uri.parse('$baseUrl/reviews/item/$itemId/stats'),
    );

    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      return {
        'averageRating': 0.0,
        'totalReviews': 0,
        'ratingDistribution': [0, 0, 0, 0, 0],
      };
    }
  }

  // Neue Bewertung erstellen
  static Future<void> createReview({
    required int rentalId,
    required int rating,
    String? comment,
  }) async {
    final response = await http.post(
      Uri.parse('$baseUrl/reviews'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'rentalId': rentalId,
        'rating': rating,
        'comment': comment,
      }),
    );

    if (response.statusCode != 201) {
      final errorData = jsonDecode(response.body);
      throw Exception(
        errorData['message'] ?? 'Bewertung konnte nicht erstellt werden',
      );
    }
  }
}
