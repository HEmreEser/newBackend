class Item {
  final int id;
  final String name;
  final String? size;
  final bool available;
  final String? description;
  final String? brand;
  final String location;
  final String gender;
  final String category;
  final String subcategory;
  final String zustand;

  Item({
    required this.id,
    required this.name,
    this.size,
    required this.available,
    this.description,
    this.brand,
    required this.location,
    required this.gender,
    required this.category,
    required this.subcategory,
    required this.zustand,
  });

  factory Item.fromJson(Map<String, dynamic> json) {
    return Item(
      id: json['id'],
      name: json['name'],
      size: json['size'],
      available: json['available'],
      description: json['description'],
      brand: json['brand'],
      location: json['location'],
      gender: json['gender'],
      category: json['category'],
      subcategory: json['subcategory'],
      zustand: json['zustand'],
    );
  }
}
