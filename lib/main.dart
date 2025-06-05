import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:kreisel_frontend/pages/login_page.dart';

void main() {
  runApp(KreiselApp());
}

class KreiselApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'HM Sportsgear',
      theme: ThemeData.dark().copyWith(
        primaryColor: Color(0xFF007AFF),
        scaffoldBackgroundColor: Color(0xFF000000),
        cardColor: Color(0xFF1C1C1E),
        dividerColor: Color(0xFF38383A),
      ),
      home: LoginPage(),
      debugShowCheckedModeBanner: false,
    );
  }
}
