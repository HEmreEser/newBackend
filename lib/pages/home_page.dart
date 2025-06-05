import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:kreisel_frontend/models/item_model.dart';
import 'package:kreisel_frontend/pages/my_rentals_page.dart';
import 'package:kreisel_frontend/services/api_service.dart';
import 'package:kreisel_frontend/services/review_api_service.dart';
import 'package:kreisel_frontend/pages/item_detail_page.dart';
import 'package:kreisel_frontend/widgets/rent_item_dialog.dart';

class HomePage extends StatefulWidget {
  final String selectedLocation;
  final String locationDisplayName;

  const HomePage({
    Key? key,
    required this.selectedLocation,
    required this.locationDisplayName,
  }) : super(key: key);

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  final TextEditingController _searchController = TextEditingController();
  final ScrollController _scrollController = ScrollController();
  List<Item> _items = [];
  List<Item> _filteredItems = [];
  bool _isLoading = true;
  bool _showOnlyAvailable = true;
  String? _selectedGender;
  String? _selectedCategory;
  String? _selectedSubcategory;
  String _sortBy = 'name'; // 'name', 'rating', 'availability'

  final Map<String, List<String>> categorySubcategories = {
    'KLEIDUNG': ['HOSEN', 'JACKEN'],
    'SCHUHE': ['STIEFEL', 'WANDERSCHUHE'],
    'ACCESSOIRES': ['MUETZEN', 'HANDSCHUHE', 'SCHALS', 'BRILLEN'],
    'TASCHEN': [],
    'EQUIPMENT': ['FLASCHEN', 'SKI', 'SNOWBOARDS', 'HELME'],
  };

  @override
  void initState() {
    super.initState();
    _loadItems();
    _searchController.addListener(_filterItems);
  }

  @override
  void dispose() {
    _searchController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  Future<void> _loadItems() async {
    if (!mounted) return;

    setState(() => _isLoading = true);

    try {
      final items = await ApiService.getItems(
        location: widget.selectedLocation,
      );
      if (mounted) {
        setState(() {
          _items = items;
          _isLoading = false;
        });
        _filterItems();
      }
    } catch (e) {
      if (mounted) {
        setState(() => _isLoading = false);
        _showAlert(
          'Fehler',
          'Items konnten nicht geladen werden: ${e.toString()}',
        );
      }
    }
  }

  void _filterItems() {
    if (!mounted) return;

    setState(() {
      _filteredItems =
          _items.where((item) {
            // Verfügbarkeitsfilter
            if (_showOnlyAvailable && !item.available) return false;

            // Suchfilter
            if (_searchController.text.isNotEmpty) {
              final query = _searchController.text.toLowerCase();
              final searchFields = [
                item.name.toLowerCase(),
                item.brand?.toLowerCase() ?? '',
                item.description?.toLowerCase() ?? '',
                item.category.toLowerCase(),
                item.subcategory.toLowerCase(),
              ];

              if (!searchFields.any((field) => field.contains(query))) {
                return false;
              }
            }

            // Kategoriefilter
            if (_selectedGender != null && item.gender != _selectedGender)
              return false;
            if (_selectedCategory != null && item.category != _selectedCategory)
              return false;
            if (_selectedSubcategory != null &&
                item.subcategory != _selectedSubcategory)
              return false;

            return true;
          }).toList();

      // Sortierung anwenden
      _sortItems();
    });
  }

  void _sortItems() {
    switch (_sortBy) {
      case 'name':
        _filteredItems.sort((a, b) => a.name.compareTo(b.name));
        break;
      case 'availability':
        _filteredItems.sort(
          (a, b) => b.available.toString().compareTo(a.available.toString()),
        );
        break;
      // Rating-Sortierung würde hier implementiert werden
    }
  }

  void _clearAllFilters() {
    setState(() {
      _searchController.clear();
      _selectedGender = null;
      _selectedCategory = null;
      _selectedSubcategory = null;
      _showOnlyAvailable = false;
    });
    _filterItems();
  }

  bool get _hasActiveFilters {
    return _searchController.text.isNotEmpty ||
        _selectedGender != null ||
        _selectedCategory != null ||
        _selectedSubcategory != null ||
        !_showOnlyAvailable;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      body: SafeArea(
        child: RefreshIndicator(
          onRefresh: _loadItems,
          color: Color(0xFF007AFF),
          backgroundColor: Color(0xFF1C1C1E),
          child: Column(
            children: [
              _buildHeader(),
              _buildFiltersSection(),
              _buildSortAndStatsBar(),
              _buildItemsList(),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildHeader() {
    return Container(
      padding: EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              CupertinoButton(
                padding: EdgeInsets.zero,
                onPressed: () => Navigator.pop(context),
                child: Icon(CupertinoIcons.back, color: Color(0xFF007AFF)),
              ),
              SizedBox(width: 16),
              Expanded(
                child: Text(
                  widget.locationDisplayName,
                  style: TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                    color: Colors.white,
                  ),
                ),
              ),
              CupertinoButton(
                padding: EdgeInsets.zero,
                onPressed: () {
                  Navigator.push(
                    context,
                    CupertinoPageRoute(builder: (context) => MyRentalsPage()),
                  );
                },
                child: Icon(
                  CupertinoIcons.person_2,
                  color: Color(0xFF007AFF),
                  size: 28,
                ),
              ),
            ],
          ),
          SizedBox(height: 16),
          // Suchleiste
          Container(
            decoration: BoxDecoration(
              color: Color(0xFF1C1C1E),
              borderRadius: BorderRadius.circular(16),
            ),
            child: TextField(
              controller: _searchController,
              style: TextStyle(color: Colors.white),
              decoration: InputDecoration(
                hintText: 'Suchen nach Name, Marke, Kategorie...',
                hintStyle: TextStyle(color: Colors.grey),
                prefixIcon: Icon(CupertinoIcons.search, color: Colors.grey),
                suffixIcon:
                    _searchController.text.isNotEmpty
                        ? CupertinoButton(
                          padding: EdgeInsets.zero,
                          onPressed: () {
                            _searchController.clear();
                            _filterItems();
                          },
                          child: Icon(
                            CupertinoIcons.clear_circled_solid,
                            color: Colors.grey,
                            size: 20,
                          ),
                        )
                        : null,
                border: InputBorder.none,
                contentPadding: EdgeInsets.all(16),
              ),
            ),
          ),
          SizedBox(height: 16),
          // Verfügbarkeits-Toggle
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                children: [
                  Text(
                    'Nur verfügbare:',
                    style: TextStyle(color: Colors.white),
                  ),
                  SizedBox(width: 8),
                  CupertinoSwitch(
                    value: _showOnlyAvailable,
                    onChanged: (value) {
                      setState(() => _showOnlyAvailable = value);
                      _filterItems();
                    },
                  ),
                ],
              ),
              if (_hasActiveFilters)
                CupertinoButton(
                  padding: EdgeInsets.zero,
                  onPressed: _clearAllFilters,
                  child: Text(
                    'Filter zurücksetzen',
                    style: TextStyle(color: Color(0xFF007AFF), fontSize: 14),
                  ),
                ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildFiltersSection() {
    return Container(
      height: 120,
      child: SingleChildScrollView(
        scrollDirection: Axis.horizontal,
        padding: EdgeInsets.symmetric(horizontal: 24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Gender Filter
            Row(
              children: [
                _buildFilterChip('DAMEN', _selectedGender, (value) {
                  setState(() {
                    _selectedGender = _selectedGender == value ? null : value;
                  });
                  _filterItems();
                }),
                _buildFilterChip('HERREN', _selectedGender, (value) {
                  setState(() {
                    _selectedGender = _selectedGender == value ? null : value;
                  });
                  _filterItems();
                }),
                _buildFilterChip('UNISEX', _selectedGender, (value) {
                  setState(() {
                    _selectedGender = _selectedGender == value ? null : value;
                  });
                  _filterItems();
                }),
              ],
            ),
            SizedBox(height: 8),
            // Category Filter
            Row(
              children:
                  categorySubcategories.keys.map((category) {
                    return _buildFilterChip(category, _selectedCategory, (
                      value,
                    ) {
                      setState(() {
                        _selectedCategory =
                            _selectedCategory == value ? null : value;
                        _selectedSubcategory = null; // Reset subcategory
                      });
                      _filterItems();
                    });
                  }).toList(),
            ),
            SizedBox(height: 8),
            // Subcategory Filter
            if (_selectedCategory != null &&
                categorySubcategories[_selectedCategory]!.isNotEmpty)
              Row(
                children:
                    categorySubcategories[_selectedCategory]!.map((
                      subcategory,
                    ) {
                      return _buildFilterChip(
                        subcategory,
                        _selectedSubcategory,
                        (value) {
                          setState(() {
                            _selectedSubcategory =
                                _selectedSubcategory == value ? null : value;
                          });
                          _filterItems();
                        },
                      );
                    }).toList(),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildSortAndStatsBar() {
    final availableCount =
        _filteredItems.where((item) => item.available).length;
    final totalCount = _filteredItems.length;

    return Container(
      padding: EdgeInsets.symmetric(horizontal: 24, vertical: 8),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            '$totalCount Items ($availableCount verfügbar)',
            style: TextStyle(color: Colors.grey, fontSize: 14),
          ),
          // Sortier-Dropdown könnte hier hinzugefügt werden
        ],
      ),
    );
  }

  Widget _buildItemsList() {
    return Expanded(
      child:
          _isLoading
              ? Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    CupertinoActivityIndicator(),
                    SizedBox(height: 16),
                    Text('Lade Items...', style: TextStyle(color: Colors.grey)),
                  ],
                ),
              )
              : _filteredItems.isEmpty
              ? _buildEmptyState()
              : ListView.builder(
                controller: _scrollController,
                padding: EdgeInsets.all(24),
                itemCount: _filteredItems.length,
                itemBuilder: (context, index) {
                  return _buildItemCard(_filteredItems[index]);
                },
              ),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(CupertinoIcons.cube_box, size: 64, color: Colors.grey),
          SizedBox(height: 16),
          Text(
            'Keine Items gefunden',
            style: TextStyle(
              color: Colors.grey,
              fontSize: 18,
              fontWeight: FontWeight.w500,
            ),
          ),
          SizedBox(height: 8),
          Text(
            _hasActiveFilters
                ? 'Versuche andere Filter oder setze sie zurück'
                : 'An diesem Standort sind keine Items verfügbar',
            style: TextStyle(color: Colors.grey, fontSize: 14),
            textAlign: TextAlign.center,
          ),
          if (_hasActiveFilters) ...[
            SizedBox(height: 16),
            CupertinoButton(
              color: Color(0xFF007AFF),
              onPressed: _clearAllFilters,
              child: Text('Filter zurücksetzen'),
            ),
          ],
        ],
      ),
    );
  }

  Widget _buildFilterChip(
    String label,
    String? selectedValue,
    Function(String) onTap,
  ) {
    final isSelected = selectedValue == label;
    return Container(
      margin: EdgeInsets.only(right: 8),
      child: CupertinoButton(
        padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        color: isSelected ? Color(0xFF007AFF) : Color(0xFF1C1C1E),
        borderRadius: BorderRadius.circular(20),
        minSize: 0,
        onPressed: () => onTap(label),
        child: Text(
          _formatChipLabel(label),
          style: TextStyle(
            color: isSelected ? Colors.white : Colors.grey,
            fontSize: 14,
          ),
        ),
      ),
    );
  }

  String _formatChipLabel(String label) {
    // Bessere Formatierung der Chip-Labels
    switch (label) {
      case 'MUETZEN':
        return 'mützen';
      case 'SCHUHE':
        return 'schuhe';
      case 'KLEIDUNG':
        return 'kleidung';
      default:
        return label.toLowerCase().replaceAll('_', ' ');
    }
  }

  Widget _buildInfoChip(String label) {
    return Container(
      margin: EdgeInsets.only(right: 8),
      padding: EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: Color(0xFF2C2C2E),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(
        _formatChipLabel(label),
        style: TextStyle(color: Colors.white70, fontSize: 12),
      ),
    );
  }

  Widget _buildItemCard(Item item) {
    return GestureDetector(
      onTap: () => _navigateToItemDetail(item),
      child: Container(
        margin: EdgeInsets.only(bottom: 16),
        decoration: BoxDecoration(
          color: Color(0xFF1C1C1E),
          borderRadius: BorderRadius.circular(16),
          border: Border.all(
            color:
                item.available
                    ? Color(0xFF32D74B).withOpacity(0.3)
                    : Color(0xFFFF453A).withOpacity(0.3),
            width: 1,
          ),
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
                      item.name,
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                        color: Colors.white,
                      ),
                    ),
                  ),
                  Container(
                    padding: EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    decoration: BoxDecoration(
                      color:
                          item.available
                              ? Color(0xFF32D74B)
                              : Color(0xFFFF453A),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Text(
                      item.available ? 'Verfügbar' : 'Ausgeliehen',
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 12,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ],
              ),
              SizedBox(height: 8),
              Row(
                children: [
                  if (item.brand != null) ...[
                    Text(
                      item.brand!,
                      style: TextStyle(color: Colors.grey, fontSize: 14),
                    ),
                    SizedBox(width: 8),
                  ],
                  _buildRatingWidget(item),
                ],
              ),
              if (item.size != null) ...[
                SizedBox(height: 4),
                Text(
                  'Größe: ${item.size}',
                  style: TextStyle(color: Colors.grey, fontSize: 14),
                ),
              ],
              if (item.description != null) ...[
                SizedBox(height: 8),
                Text(
                  item.description!,
                  style: TextStyle(color: Colors.white70, fontSize: 14),
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
              ],
              SizedBox(height: 12),
              Wrap(
                spacing: 8,
                runSpacing: 4,
                children: [
                  _buildInfoChip(item.gender),
                  _buildInfoChip(item.category),
                  _buildInfoChip(item.subcategory),
                  if (item.zustand != null) _buildInfoChip(item.zustand!),
                ],
              ),
              SizedBox(height: 16),
              Row(
                children: [
                  Expanded(
                    child: CupertinoButton(
                      padding: EdgeInsets.symmetric(vertical: 12),
                      color: Color(0xFF2C2C2E),
                      borderRadius: BorderRadius.circular(8),
                      onPressed: () => _navigateToItemDetail(item),
                      child: Text(
                        'Details anzeigen',
                        style: TextStyle(color: Colors.white, fontSize: 14),
                      ),
                    ),
                  ),
                  if (item.available) ...[
                    SizedBox(width: 12),
                    Expanded(
                      child: CupertinoButton(
                        padding: EdgeInsets.symmetric(vertical: 12),
                        color: Color(0xFF007AFF),
                        borderRadius: BorderRadius.circular(8),
                        onPressed: () => _showRentDialog(item),
                        child: Text(
                          'Ausleihen',
                          style: TextStyle(
                            fontSize: 14,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ),
                    ),
                  ],
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildRatingWidget(Item item) {
    return FutureBuilder<Map<String, dynamic>>(
      future: ReviewApiService.getItemRatingStats(item.id),
      builder: (context, snapshot) {
        if (snapshot.hasData && snapshot.data!['totalReviews'] > 0) {
          return Row(
            children: [
              Icon(
                CupertinoIcons.star_fill,
                color: Color(0xFFFFD60A),
                size: 14,
              ),
              SizedBox(width: 4),
              Text(
                '${snapshot.data!['averageRating'].toStringAsFixed(1)}',
                style: TextStyle(color: Colors.white70, fontSize: 12),
              ),
              SizedBox(width: 4),
              Text(
                '(${snapshot.data!['totalReviews']})',
                style: TextStyle(color: Colors.grey, fontSize: 12),
              ),
            ],
          );
        }
        return SizedBox.shrink();
      },
    );
  }

  void _navigateToItemDetail(Item item) {
    Navigator.push(
      context,
      CupertinoPageRoute(
        builder:
            (context) => ItemDetailPage(item: item, onItemUpdated: _loadItems),
      ),
    );
  }

  void _showRentDialog(Item item) {
    showCupertinoDialog(
      context: context,
      builder: (context) => RentItemDialog(item: item, onRented: _loadItems),
    );
  }

  void _showAlert(String title, String message) {
    if (!mounted) return;

    showCupertinoDialog(
      context: context,
      builder:
          (context) => CupertinoAlertDialog(
            title: Text(title),
            content: Text(message),
            actions: [
              CupertinoDialogAction(
                child: Text('OK'),
                onPressed: () => Navigator.pop(context),
              ),
            ],
          ),
    );
  }
}
