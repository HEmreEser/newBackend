import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:kreisel_frontend/models/item_model.dart';
import 'package:kreisel_frontend/models/review_model.dart';
import 'package:kreisel_frontend/services/api_service.dart';
import 'package:kreisel_frontend/services/review_api_service.dart';
import 'package:kreisel_frontend/widgets/rent_item_dialog.dart';

class ItemDetailPage extends StatefulWidget {
  final Item item;
  final VoidCallback? onItemUpdated;

  const ItemDetailPage({Key? key, required this.item, this.onItemUpdated})
    : super(key: key);

  @override
  _ItemDetailPageState createState() => _ItemDetailPageState();
}

class _ItemDetailPageState extends State<ItemDetailPage> {
  List<Review> reviews = [];
  Map<String, dynamic> ratingStats = {
    'averageRating': 0.0,
    'totalReviews': 0,
    'ratingDistribution': [0, 0, 0, 0, 0],
  };
  bool isLoadingReviews = true;

  @override
  void initState() {
    super.initState();
    _loadReviews();
  }

  Future<void> _loadReviews() async {
    try {
      final [reviewsData, statsData] = await Future.wait([
        ReviewApiService.getItemReviews(widget.item.id),
        ReviewApiService.getItemRatingStats(widget.item.id),
      ]);

      setState(() {
        reviews = reviewsData as List<Review>;
        ratingStats = statsData as Map<String, dynamic>;
        isLoadingReviews = false;
      });
    } catch (e) {
      setState(() => isLoadingReviews = false);
      print('Fehler beim Laden der Bewertungen: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      body: SafeArea(
        child: CustomScrollView(
          slivers: [
            // Header mit Back Button
            SliverToBoxAdapter(
              child: Container(
                padding: EdgeInsets.all(24),
                child: Row(
                  children: [
                    CupertinoButton(
                      padding: EdgeInsets.zero,
                      onPressed: () => Navigator.pop(context),
                      child: Icon(
                        CupertinoIcons.back,
                        color: Color(0xFF007AFF),
                        size: 28,
                      ),
                    ),
                    SizedBox(width: 16),
                    Expanded(
                      child: Text(
                        'Item Details',
                        style: TextStyle(
                          fontSize: 24,
                          fontWeight: FontWeight.bold,
                          color: Colors.white,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),

            // Item Info Card
            SliverToBoxAdapter(
              child: Container(
                margin: EdgeInsets.symmetric(horizontal: 24, vertical: 8),
                decoration: BoxDecoration(
                  color: Color(0xFF1C1C1E),
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(
                    color:
                        widget.item.available
                            ? Color(0xFF32D74B).withOpacity(0.3)
                            : Color(0xFFFF453A).withOpacity(0.3),
                    width: 1,
                  ),
                ),
                child: Padding(
                  padding: EdgeInsets.all(20),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      // Item Name und Status
                      Row(
                        children: [
                          Expanded(
                            child: Text(
                              widget.item.name,
                              style: TextStyle(
                                fontSize: 24,
                                fontWeight: FontWeight.bold,
                                color: Colors.white,
                              ),
                            ),
                          ),
                          Container(
                            padding: EdgeInsets.symmetric(
                              horizontal: 12,
                              vertical: 6,
                            ),
                            decoration: BoxDecoration(
                              color:
                                  widget.item.available
                                      ? Color(0xFF32D74B)
                                      : Color(0xFFFF453A),
                              borderRadius: BorderRadius.circular(8),
                            ),
                            child: Text(
                              widget.item.available
                                  ? 'Verfügbar'
                                  : 'Ausgeliehen',
                              style: TextStyle(
                                color: Colors.white,
                                fontSize: 12,
                                fontWeight: FontWeight.w600,
                              ),
                            ),
                          ),
                        ],
                      ),

                      SizedBox(height: 16),

                      // Bewertung Section
                      if (ratingStats['totalReviews'] > 0) ...[
                        Row(
                          children: [
                            Icon(
                              CupertinoIcons.star_fill,
                              color: Color(0xFFFFD60A),
                              size: 20,
                            ),
                            SizedBox(width: 8),
                            Text(
                              '${ratingStats['averageRating'].toStringAsFixed(1)}',
                              style: TextStyle(
                                fontSize: 18,
                                fontWeight: FontWeight.bold,
                                color: Colors.white,
                              ),
                            ),
                            SizedBox(width: 8),
                            Text(
                              '(${ratingStats['totalReviews']} Bewertungen)',
                              style: TextStyle(
                                color: Colors.grey,
                                fontSize: 14,
                              ),
                            ),
                          ],
                        ),
                        SizedBox(height: 12),
                        _buildRatingDistribution(),
                        SizedBox(height: 16),
                      ],

                      // Brand und Größe
                      if (widget.item.brand != null) ...[
                        _buildInfoRow('Marke', widget.item.brand!),
                        SizedBox(height: 8),
                      ],
                      if (widget.item.size != null) ...[
                        _buildInfoRow('Größe', widget.item.size!),
                        SizedBox(height: 8),
                      ],
                      _buildInfoRow('Zustand', widget.item.zustand),
                      SizedBox(height: 8),
                      _buildInfoRow('Standort', widget.item.location),

                      // Beschreibung
                      if (widget.item.description != null) ...[
                        SizedBox(height: 16),
                        Text(
                          'Beschreibung',
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                            color: Colors.white,
                          ),
                        ),
                        SizedBox(height: 8),
                        Text(
                          widget.item.description!,
                          style: TextStyle(
                            color: Colors.white70,
                            fontSize: 14,
                            height: 1.4,
                          ),
                        ),
                      ],

                      SizedBox(height: 16),

                      // Category Tags
                      Wrap(
                        spacing: 8,
                        runSpacing: 8,
                        children: [
                          _buildTag(widget.item.gender),
                          _buildTag(widget.item.category),
                          _buildTag(widget.item.subcategory),
                        ],
                      ),

                      // Ausleihen Button
                      if (widget.item.available) ...[
                        SizedBox(height: 20),
                        Container(
                          width: double.infinity,
                          child: CupertinoButton(
                            color: Color(0xFF007AFF),
                            borderRadius: BorderRadius.circular(12),
                            onPressed: () {
                              showCupertinoDialog(
                                context: context,
                                builder:
                                    (context) => RentItemDialog(
                                      item: widget.item,
                                      onRented: () {
                                        if (widget.onItemUpdated != null) {
                                          widget.onItemUpdated!();
                                        }
                                        Navigator.pop(context);
                                      },
                                    ),
                              );
                            },
                            child: Text(
                              'Jetzt ausleihen',
                              style: TextStyle(
                                fontSize: 16,
                                fontWeight: FontWeight.w600,
                              ),
                            ),
                          ),
                        ),
                      ],
                    ],
                  ),
                ),
              ),
            ),

            // Reviews Section
            SliverToBoxAdapter(
              child: Container(
                margin: EdgeInsets.symmetric(horizontal: 24, vertical: 16),
                child: Text(
                  'Bewertungen (${reviews.length})',
                  style: TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                    color: Colors.white,
                  ),
                ),
              ),
            ),

            // Reviews List
            if (isLoadingReviews)
              SliverToBoxAdapter(
                child: Center(
                  child: Padding(
                    padding: EdgeInsets.all(32),
                    child: CupertinoActivityIndicator(),
                  ),
                ),
              )
            else if (reviews.isEmpty)
              SliverToBoxAdapter(
                child: Container(
                  margin: EdgeInsets.symmetric(horizontal: 24),
                  padding: EdgeInsets.all(32),
                  child: Center(
                    child: Text(
                      'Noch keine Bewertungen vorhanden',
                      style: TextStyle(color: Colors.grey, fontSize: 16),
                    ),
                  ),
                ),
              )
            else
              SliverList(
                delegate: SliverChildBuilderDelegate((context, index) {
                  return _buildReviewCard(reviews[index]);
                }, childCount: reviews.length),
              ),

            // Bottom Spacing
            SliverToBoxAdapter(child: SizedBox(height: 32)),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoRow(String label, String value) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SizedBox(
          width: 80,
          child: Text(
            '$label:',
            style: TextStyle(color: Colors.grey, fontSize: 14),
          ),
        ),
        Expanded(
          child: Text(
            value,
            style: TextStyle(
              color: Colors.white,
              fontSize: 14,
              fontWeight: FontWeight.w500,
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildTag(String text) {
    return Container(
      padding: EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: Color(0xFF2C2C2E),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(
        text.toLowerCase(),
        style: TextStyle(color: Colors.white70, fontSize: 12),
      ),
    );
  }

  Widget _buildRatingDistribution() {
    List<int> distribution = List<int>.from(ratingStats['ratingDistribution']);
    int totalReviews = ratingStats['totalReviews'];

    return Column(
      children: List.generate(5, (index) {
        int starCount = 5 - index;
        int count = distribution[starCount - 1];
        double percentage = totalReviews > 0 ? count / totalReviews : 0.0;

        return Padding(
          padding: EdgeInsets.symmetric(vertical: 2),
          child: Row(
            children: [
              Text(
                '$starCount',
                style: TextStyle(color: Colors.white70, fontSize: 12),
              ),
              SizedBox(width: 4),
              Icon(
                CupertinoIcons.star_fill,
                color: Color(0xFFFFD60A),
                size: 12,
              ),
              SizedBox(width: 8),
              Expanded(
                child: Container(
                  height: 8,
                  decoration: BoxDecoration(
                    color: Color(0xFF2C2C2E),
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: FractionallySizedBox(
                    alignment: Alignment.centerLeft,
                    widthFactor: percentage,
                    child: Container(
                      decoration: BoxDecoration(
                        color: Color(0xFFFFD60A),
                        borderRadius: BorderRadius.circular(4),
                      ),
                    ),
                  ),
                ),
              ),
              SizedBox(width: 8),
              Text(
                '$count',
                style: TextStyle(color: Colors.white70, fontSize: 12),
              ),
            ],
          ),
        );
      }),
    );
  }

  Widget _buildReviewCard(Review review) {
    return Container(
      margin: EdgeInsets.symmetric(horizontal: 24, vertical: 8),
      decoration: BoxDecoration(
        color: Color(0xFF1C1C1E),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Expanded(
                  child: Text(
                    review.userName,
                    style: TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                      color: Colors.white,
                    ),
                  ),
                ),
                Row(
                  children: List.generate(5, (index) {
                    return Icon(
                      index < review.rating
                          ? CupertinoIcons.star_fill
                          : CupertinoIcons.star,
                      color: Color(0xFFFFD60A),
                      size: 16,
                    );
                  }),
                ),
              ],
            ),
            SizedBox(height: 8),
            Text(
              _formatDate(review.createdAt),
              style: TextStyle(color: Colors.grey, fontSize: 12),
            ),
            if (review.comment != null && review.comment!.isNotEmpty) ...[
              SizedBox(height: 12),
              Text(
                review.comment!,
                style: TextStyle(
                  color: Colors.white70,
                  fontSize: 14,
                  height: 1.4,
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  String _formatDate(DateTime date) {
    final now = DateTime.now();
    final difference = now.difference(date);

    if (difference.inDays > 30) {
      return '${date.day}.${date.month}.${date.year}';
    } else if (difference.inDays > 0) {
      return 'vor ${difference.inDays} Tag${difference.inDays == 1 ? '' : 'en'}';
    } else if (difference.inHours > 0) {
      return 'vor ${difference.inHours} Stunde${difference.inHours == 1 ? '' : 'n'}';
    } else {
      return 'vor kurzem';
    }
  }
}
