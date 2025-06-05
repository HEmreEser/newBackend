import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:kreisel_frontend/models/user_model.dart';
import 'package:kreisel_frontend/models/item_model.dart';
import 'package:kreisel_frontend/models/rental_model.dart';

class ApiService {
  static const String baseUrl = 'http://localhost:8080/api';
  static String? authToken;
  static User? currentUser;

  // Helper method to add auth headers
  static Map<String, String> _getHeaders() {
    return {
      'Content-Type': 'application/json',
      if (authToken != null) 'Authorization': 'Bearer $authToken',
    };
  }

  // Initialize service with saved token
  static Future<void> initialize() async {
    final prefs = await SharedPreferences.getInstance();
    authToken = prefs.getString('auth_token');
    if (authToken != null) {
      try {
        // Verify token is still valid by fetching user data
        final userResponse = await http.get(
          Uri.parse('$baseUrl/auth/me'),
          headers: _getHeaders(),
        );
        if (userResponse.statusCode == 200) {
          currentUser = User.fromJson(jsonDecode(userResponse.body));
        } else {
          await _clearAuthData();
        }
      } catch (e) {
        await _clearAuthData();
      }
    }
  }

  // Clear auth data
  static Future<void> _clearAuthData() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('auth_token');
    authToken = null;
    currentUser = null;
  }

  // Login
  static Future<User> login(String email, String password) async {
    final response = await http.post(
      Uri.parse('$baseUrl/auth/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'email': email, 'password': password}),
    );

    if (response.statusCode == 200) {
      final responseData = jsonDecode(response.body);
      authToken = responseData['token'];
      currentUser = User.fromJson(responseData);

      // Save token
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString('auth_token', authToken!);

      return currentUser!;
    } else {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Login fehlgeschlagen');
    }
  }

  // Register
  static Future<User> register(
    String fullName,
    String email,
    String password,
  ) async {
    final response = await http.post(
      Uri.parse('$baseUrl/auth/register'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'fullName': fullName,
        'email': email,
        'password': password,
      }),
    );

    if (response.statusCode == 200) {
      final responseData = jsonDecode(response.body);
      authToken = responseData['token'];
      currentUser = User.fromJson(responseData);

      // Save token
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString('auth_token', authToken!);

      return currentUser!;
    } else {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Registrierung fehlgeschlagen');
    }
  }

  // Logout
  static Future<void> logout() async {
    if (authToken == null) return;

    try {
      await http.post(
        Uri.parse('$baseUrl/auth/logout'),
        headers: _getHeaders(),
      );
    } finally {
      await _clearAuthData();
    }
  }

  // Get items with filters
  static Future<List<Item>> getItems({
    required String location,
    bool? available,
    String? searchQuery,
    String? gender,
    String? category,
    String? subcategory,
    String? size,
  }) async {
    var params = {'location': location};
    if (available != null) params['available'] = available.toString();
    if (searchQuery != null && searchQuery.isNotEmpty)
      params['searchQuery'] = searchQuery;
    if (gender != null) params['gender'] = gender;
    if (category != null) params['category'] = category;
    if (subcategory != null) params['subcategory'] = subcategory;
    if (size != null) params['size'] = size;

    final uri = Uri.parse('$baseUrl/items').replace(queryParameters: params);
    final response = await http.get(uri, headers: _getHeaders());

    if (response.statusCode == 200) {
      List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => Item.fromJson(json)).toList();
    } else {
      throw Exception('Fehler beim Laden der Items');
    }
  }

  // Rent item
  static Future<void> rentItem(int itemId, String endDate) async {
    if (currentUser == null) throw Exception('Benutzer nicht angemeldet');

    final response = await http.post(
      Uri.parse('$baseUrl/rentals/user/${currentUser!.userId}/rent'),
      headers: _getHeaders(),
      body: jsonEncode({'itemId': itemId, 'endDate': endDate}),
    );

    if (response.statusCode != 200) {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Ausleihe fehlgeschlagen');
    }
  }

  // Get user rentals
  static Future<List<Rental>> getUserRentals() async {
    if (currentUser == null) throw Exception('Benutzer nicht angemeldet');

    final response = await http.get(
      Uri.parse('$baseUrl/rentals/user/${currentUser!.userId}'),
      headers: _getHeaders(),
    );

    if (response.statusCode == 200) {
      List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => Rental.fromJson(json)).toList();
    } else {
      throw Exception('Fehler beim Laden der Ausleihen');
    }
  }

  // Return item
  static Future<void> returnItem(int rentalId) async {
    final response = await http.put(
      Uri.parse('$baseUrl/rentals/$rentalId/return'),
      headers: _getHeaders(),
    );

    if (response.statusCode != 200) {
      throw Exception('Rückgabe fehlgeschlagen');
    }
  }

  // Extend rental
  static Future<void> extendRental(int rentalId, DateTime newEndDate) async {
    final formattedDate =
        "${newEndDate.year}-${newEndDate.month.toString().padLeft(2, '0')}-${newEndDate.day.toString().padLeft(2, '0')}";

    final response = await http.put(
      Uri.parse('$baseUrl/rentals/$rentalId/extend'),
      headers: _getHeaders(),
      body: jsonEncode({'newEndDate': formattedDate}),
    );

    if (response.statusCode != 200) {
      throw Exception('Verlängerung fehlgeschlagen');
    }
  }
}
